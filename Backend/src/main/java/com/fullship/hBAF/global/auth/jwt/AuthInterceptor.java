package com.fullship.hBAF.global.auth.jwt;

import com.fullship.hBAF.domain.member.service.MemberService;
import com.fullship.hBAF.domain.member.service.command.FindMemberByIdCommand;
import com.fullship.hBAF.global.auth.entity.RedisRefreshToken;
import com.fullship.hBAF.global.auth.repository.RefreshTokenRepository;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import com.fullship.hBAF.util.Auth;
import com.fullship.hBAF.util.CookieProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String REFRESH_TOKEN_NAME = "refreshToken";
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthTokenGenerator authTokenGenerator;
    private final CookieProvider cookieProvider;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("Login Interceptor preHandlerpreHandler");
        if (HttpMethod.OPTIONS.matches(request.getMethod())){
            return true;
        }
        if (!checkAnnotation(handler, Auth.class)){//@Auth 어노테이션 없으면
            return true; // 로그인 검증 넘어감
        }

        //JWT 추출
        String accessToken = resolveTokenInRequest(request);
        String refreshToken = getRefreshToken(request);

        try {//AT 유효
            jwtTokenProvider.validateAccessToken(accessToken);
            return allowAccess(response, accessToken);
        } catch (ExpiredJwtException e) {//AT만료
            jwtTokenProvider.validateRefreshToken(refreshToken);
            String strMemberId = getMemberIdFromToken(refreshToken);
            RedisRefreshToken redisRefreshToken = refreshTokenRepository.findById(strMemberId).orElseThrow(
                    () -> new CustomException(ErrorCode.TOKEN_NOT_FOUND)
            );
            if (jwtTokenProvider.validateRefreshToken(redisRefreshToken.getRefreshToken())){
                return allowAccess(response, accessToken);
            }
        }
        throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    /**
     * 검증된 접근일 시 토큰을 재발급하고 접근을 허가하는 메서드
     * @param response
     * @param accessToken
     * @return
     */
    private boolean allowAccess(HttpServletResponse response, String accessToken) {
        //유저 존재여부 확인
        String strMemberId = getMemberIdFromToken(accessToken);
        long memberId = Long.parseLong(strMemberId);
        FindMemberByIdCommand command = FindMemberByIdCommand.builder()
                .id(Long.parseLong(strMemberId))
                .build();
        memberService.findMemberById(command);

        //AT RT 재발급
        AccessToken newAccessToken = authTokenGenerator.generateAT(memberId);
        RefreshToken newRefreshToken = authTokenGenerator.generateRT(memberId);
        Cookie cookie = cookieProvider.createCookie(
                "refreshToken",
                newRefreshToken.getRefreshToken(),
                Long.valueOf(newRefreshToken.getExpiresIn()/1000L).intValue()
        );
        response.setHeader("Authorization", "Bearer " + newAccessToken.getAccessToken());
        response.addCookie(cookie);
        return true;
    }

    /**
     * header에서 토큰 추출하는 메서드
     * @param request HttpServletRequest
     * @return token string
     */
    private String resolveTokenInRequest(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        String token = jwtTokenProvider.resolveToken(bearerToken);
        if (token == null){
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
        }
        return token;
    }

    /**
     * refreshToken 추출 메서드
     * @param request
     * @return refreshToken
     */
    private String getRefreshToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies == null){
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(REFRESH_TOKEN_NAME)){
                return cookie.getValue();
            }
        }
        throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
    }

    /**
     * token으로 부터 userId를 가져오는 메서드
     * @param accessToken     * @return userEmail
     */
    private String getMemberIdFromToken(String accessToken) {
        return jwtTokenProvider.extractSubject(accessToken);
    }

    private boolean checkAnnotation(Object handler, Class<Auth> authClass) {
        //js. html 타입인 view 과련 파일들은 통과한다.(view 관련 요청 = ResourceHttpRequestHandler)
        if (handler instanceof ResourceHttpRequestHandler) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        //Auth anntotation이 있는 경우
        if (null != handlerMethod.getMethodAnnotation(authClass) || null != handlerMethod.getBeanType().getAnnotation(authClass)) {
            return true;
        }else {//annotation이 없는 경우
            return false;
        }

    }
}