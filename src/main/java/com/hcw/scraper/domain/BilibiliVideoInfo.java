package com.hcw.scraper.domain;

import lombok.Data;


@Data
public class BilibiliVideoInfo {
    private int code;
    private String message;
    private int ttl;
    private BilibiliVideoData data;
}
