package com.fallen.ultra.utils;

public class Params {

	
	public static final int NOTIFICATION_ID = 90123;
	public static final int FLAG_PLAY = 100;
	public static final int FLAG_STOP = 101;
	public static final int FLAG_BIND_ACTIVITY = 102;
	//public static final String ACTION_FROM_CONTROLS = "";
	public static final String ACTION_FROM_CONTROLS = "ACTION_FROM_CONTRLOS";
	public static final String ACTION_FROM_BROADCAST = "ACTION_FROM_BROADCAST";
	public static final String ACTION_FROM_PLAYER_FRAGMENT = "ACTION_FROM_PLAYER_FRAGMENT";
	public static final int BUTTON_START_KEY = 200;
	public static final int BUTTON_STOP_KEY = 201;
	public static final String ICY_KEY = "icy-metaint";
	public static final String SOCKET_PORT = "8889";
	public static final String LOCAL_SOCKET_STREAM_IP = "http://127.0.0.1:" + SOCKET_PORT;
	
	//rewrite to int an rename with including async  word
	public static final int ACTION_SOCKET_PREACCEPT_DELAY = 300;
	public static final int ACTION_BUFFERED = 301;
	public static final int ACTION_NEW_TITLE = 302;
	public static final int ACTION_CONNECTING = 303;
	public static final int ACTION_PHONE_CALL = 304;
	public static final int ACTION_PHONE_CALL_DONE = 305;
	
	public static final int NO_METAINT = -1;
	public static final int BUFFER_FOR_PLAYER_IN_BYTES = 1000;
	public static final String DEFAULT_LOG_TYPE = "ultraTag";
	public static final int DEFAULT_INPUTSTREAM_BUFFER_BYTES = 1024;
	public static final int ASYNC_ACTION_PLAY_STREAM = 300;
	public static final CharSequence STREAM_TITLE_KEYWORD = "StreamTitle";
	public static final String TRACK_ARTIST_KEY = "artist_key";
	public static final String TRACK_SONG_KEY = "song_key";
	public static final String ULTRA_URL_HIGH = "http://mp3.nashe.ru/ultra-128.mp3";
	public static final String ULTRA_URL_LOW = "http://mp3.nashe.ru/ultra-64.mp3";
	public static final String NO_TITLE = "No Title";
	public static final Object EMPTY_ARTIST_STRING = "";
	public static final int ACTION_WRONG = -1;
		public static final int MAX_METAINT_VALUE = 4080;

	public static final String KEY_PREFERENCES_QUALITY = "quality";
	public static final String KEY_PREFERENCES_QUALITY_FIELD = "quality_field";
	public static final int QUALITY_64 = 64;
	public static final int QUALITY_128 = 128;
	public static final int QUALITY_DEFAULT_KEY = 128;
	
	
	public static final int STATUS_CONNECTING = 500;
	public static final int STATUS_BUFFERING = 501;
	public static final int STATUS_PLAYING = 502;
	public static final int STATUS_STOPED = 503;
	public static final int STATUS_ERROR = 504;
	public static final int STATUS_SOCKET_CREATING = 505;
	public static final String STATUS_DESCRIPTION_NOTHING_AT_ALL = "";
	public static final int STATUS_NEW_TITLE = 506;
	public static final boolean USE_CHECKER = false;
	public static final int STATUS_NONE = -1;
	public static final int STATUS_STREAM_ENDS = 507;
	public static final int CONNECTION_TIMEOUT = 5000;
	public static final String TEMP_FILE_NAME = "tempArt";

	
	public static String LASTFM_DEV_KEY = "73bb327ff0e5bd23f100274492105b4f";
	public static String LASTFM_TRACK_GET_INFO = "http://ws.audioscrobbler.com/2.0/?method=track.getInfo";
	public static final String LASTFM_ARTIST_GET_INFO = "http://ws.audioscrobbler.com/2.0/?method=artist.getInfo";
	
	public static String LASTFM_TRACK_CORRECTION = "http://ws.audioscrobbler.com/2.0/?method=track.getCorrection";
	public static String LASTFM_ARTIST_CORRECTION = "http://ws.audioscrobbler.com/2.0/?method=artist.getCorrection";
	
	
	
	
	
	
	
	
	
	

	}
