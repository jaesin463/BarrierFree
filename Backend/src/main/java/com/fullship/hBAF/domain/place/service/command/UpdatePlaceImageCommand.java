package com.fullship.hBAF.domain.place.service.command;

import lombok.Builder;
import lombok.Data;

import java.net.URL;

@Data
@Builder
public class UpdatePlaceImageCommand {
    private String poiId;
    private String imageUrl;
}
