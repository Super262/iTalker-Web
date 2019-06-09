package net.qiujuer.web.italker.push.bean.api.base;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ResponseModel<M> implements Serializable {

    public static final int SUCCEED = 1;

    public static final int ERROR_UNKNOWN = 0;


    public static final int ERROR_NOT_FOUND_USER = 4041;

    public static final int ERROR_NOT_FOUND_GROUP = 4042;

    public static final int ERROR_NOT_FOUND_GROUP_MEMBER = 4043;


    public static final int ERROR_CREATE_USER = 3001;

    public static final int ERROR_CREATE_GROUP = 3002;

    public static final int ERROR_CREATE_MESSAGE = 3003;


    public static final int ERROR_PARAMETERS = 4001;

    public static final int ERROR_PARAMETERS_EXIST_ACCOUNT = 4002;

    public static final int ERROR_PARAMETERS_EXIST_NAME = 4003;


    public static final int ERROR_SERVICE = 5001;


    public static final int ERROR_ACCOUNT_TOKEN = 2001;

    public static final int ERROR_ACCOUNT_LOGIN = 2002;

    public static final int ERROR_ACCOUNT_REGISTER = 2003;

    public static final int ERROR_ACCOUNT_NO_PERMISSION = 2010;

    @Expose
    private int code;
    @Expose
    private String message;
    @Expose
    private LocalDateTime time = LocalDateTime.now();
    @Expose
    private M result;

    public ResponseModel() {
        code = 1;
        message = "ok";
    }

    public ResponseModel(M result) {
        this();
        this.result = result;
    }

    public ResponseModel(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseModel(int code, String message, M result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public boolean isSucceed() {
        return code == SUCCEED;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public M getResult() {
        return result;
    }

    public void setResult(M result) {
        this.result = result;
    }

    public static <M> ResponseModel<M> buildOk() {
        return new ResponseModel<M>();
    }

    public static <M> ResponseModel<M> buildOk(M result) {
        return new ResponseModel<M>(result);
    }

    public static <M> ResponseModel<M> buildParameterError() {
        return new ResponseModel<M>(ERROR_PARAMETERS, "Parameters Error.");
    }

    public static <M> ResponseModel<M> buildHaveAccountError() {
        return new ResponseModel<M>(ERROR_PARAMETERS_EXIST_ACCOUNT, "Already have this account.");
    }

    public static <M> ResponseModel<M> buildHaveNameError() {
        return new ResponseModel<M>(ERROR_PARAMETERS_EXIST_NAME, "Already have this name.");
    }

    public static <M> ResponseModel<M> buildServiceError() {
        return new ResponseModel<M>(ERROR_SERVICE, "Service Error.");
    }

    public static <M> ResponseModel<M> buildNotFoundUserError(String str) {
        return new ResponseModel<M>(ERROR_NOT_FOUND_USER, str != null ? str : "Not Found User.");
    }

    public static <M> ResponseModel<M> buildNotFoundGroupError(String str) {
        return new ResponseModel<M>(ERROR_NOT_FOUND_GROUP, str != null ? str : "Not Found Group.");
    }

    public static <M> ResponseModel<M> buildNotFoundGroupMemberError(String str) {
        return new ResponseModel<M>(ERROR_NOT_FOUND_GROUP_MEMBER, str != null ? str : "Not Found GroupMember.");
    }

    public static <M> ResponseModel<M> buildAccountError() {
        return new ResponseModel<M>(ERROR_ACCOUNT_TOKEN, "Account Error; you need login.");
    }

    public static <M> ResponseModel<M> buildLoginError() {
        return new ResponseModel<M>(ERROR_ACCOUNT_LOGIN, "Account or password error.");
    }

    public static <M> ResponseModel<M> buildRegisterError() {
        return new ResponseModel<M>(ERROR_ACCOUNT_REGISTER, "Have this account.");
    }

    public static <M> ResponseModel<M> buildNoPermissionError() {
        return new ResponseModel<M>(ERROR_ACCOUNT_NO_PERMISSION, "You do not have permission to operate.");
    }

    public static <M> ResponseModel<M> buildCreateError(int type) {
        return new ResponseModel<M>(type, "Create failed.");
    }

}