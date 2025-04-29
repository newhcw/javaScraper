package com.hcw.scraper.domain;

import lombok.Data;

@Data
public class BilibiliVideoData {
    private String bvid;
    private long aid;
    private int videos;
    private int tid;
    private int tid_v2;
    private String tname;
    private String tname_v2;
    private int copyright;
    private String pic;
    private String title;
    private long pubdate;
    private long ctime;
    private String desc;
    private Object desc_v2;
    private int state;
    private int duration;
    private long mission_id;
    private String dynamic;
    private long cid;
    private Object premiere;
    private int teenage_mode;
    private boolean is_chargeable_season;
    private boolean is_story;
    private boolean is_upower_exclusive;
    private boolean is_upower_play;
    private boolean is_upower_preview;
    private int enable_vt;
    private String vt_display;
    private boolean is_upower_exclusive_with_qa;
    private boolean no_cache;
    private boolean is_season_display;
    private Object honor_reply;
    private String like_icon;
    private boolean need_jump_bv;
    private boolean disable_show_up_info;
    private int is_story_play;
    private boolean is_view_self;
}
