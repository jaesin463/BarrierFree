import 'dart:convert';
import 'dart:io';
import 'package:http_parser/http_parser.dart'; // 멀티파트파일용

import 'package:http/http.dart' as http;

import 'secure_storage_service.dart';

class ReviewService {
  static const String _baseUrl = 'https://hbaf.site/api/review';
  final SecureStorageService _secureStorageService = SecureStorageService();

  //리뷰 불러오기
  Future<List<dynamic>> fetchReviewByPlaceId(String poiId) async {
    final response = await http.get(Uri.parse('$_baseUrl/list?poiId=$poiId'));

    print(response);
    print(response.body);
    print(response.statusCode);
    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      //빈배열에도 오류 안나게
      final List<dynamic> reviewList = data['list'] as List<dynamic>;

      return reviewList;
    } else {
      throw Exception('이 장소의 리뷰 목록 불러오는데 실패');
    }
  }

  //이미지 업로드
  Future<String?> uploadImage(File image) async {
    var uri = Uri.parse('$_baseUrl/img');
    var request = http.MultipartRequest('POST', uri)
      ..files.add(
        await http.MultipartFile.fromPath(
          'img',
          image.path,
          contentType: MediaType('image', 'jpg'),
        ),
      );

    var response = await request.send();

    if (response.statusCode == 200) {
      var responseData = await response.stream.toBytes();
      var responseString = String.fromCharCodes(responseData);
      var jsonResponse = json.decode(responseString);

      //image Url => 서버에서 주는 이미지 url
      print(jsonResponse['list']);
      print(jsonResponse['list'][0]);
      return jsonResponse['list'][0];
    } else {
      throw Exception('이미지 업로드 실패함');
    }
  }

  //리뷰 작성하기
  Future<bool> addReview({
    required String poiId,
    required int userId,
    required String content,
    required int feedback,
    required String? imageUrl,
  }) async {
    String? accessToken = await _secureStorageService.getToken();
    String? cookies = await _secureStorageService.getCookies();

    var response = await http.post(
      Uri.parse('$_baseUrl'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $accessToken',
        'Cookie': cookies!,
      },
      body: jsonEncode({
        'poiId': poiId,
        'memberId': userId,
        'content': content,
        'feedback': feedback,
        'img': imageUrl != null ? [imageUrl] : [],
      }),
    );
    if (response.statusCode == 200) {
      return true;
    } else {
      print(response.statusCode);
      throw Exception('리뷰업로드에러');
    }
  }
}