package com.example.b11ndboard.global.common;

public enum ResponseKind {
    SIGNUP,
    OAUTH_SIGNUP,
    LOGIN,
    OAUTH_LOGIN,
    VALIDATION_ERROR,
    LOGOUT,

    // Post related
    POST_CREATE,
    POST_GET_ALL,
    POST_GET_DETAIL,
    POST_UPDATE,
    POST_DELETE,
    POST_LIKE,
    POST_LIKE_CANCEL,
    POST_ERROR,

    // Comment related
    COMMENT_CREATE,
    COMMENT_GET_ALL,
    COMMENT_UPDATE,
    COMMENT_DELETE,
    COMMENT_ERROR
}
