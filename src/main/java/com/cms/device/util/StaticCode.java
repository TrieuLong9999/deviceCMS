package com.cms.device.util;

public class StaticCode {
    public enum CodeResponse{
        Success(200),
        Not_Found(404),
        Error(500),
        Invalid_Form_Request(600),
        Exist_Data(700);

        private final  int code;

        CodeResponse(int code) {
            this.code = code;
        }
        public int getCode() {
            return code;
        }
        public String getName(){
            return name();
        }
    }

}
