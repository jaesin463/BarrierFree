package com.fullship.hBAF.global.api.service.command;

import com.fullship.hBAF.global.api.response.GeoCode;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ElevationForPathCommand {

  private String geoCode;

  public static ElevationForPathCommand createElevateCommand(List<GeoCode> geoCode) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < geoCode.size(); i++) {
      sb.append(geoCode.get(i).getLatitude()).append(",").append(geoCode.get(i).getLongitude()).append("|");
    }

    return ElevationForPathCommand.builder()
        .geoCode(sb.toString().substring(0, sb.length() - 1))
        .build();
  }
}