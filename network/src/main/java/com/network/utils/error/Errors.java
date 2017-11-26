package com.network.utils.error;


/**
 * @author yogi
 */
public class Errors {
    public String errorMessage;
    public String errorCode;

    /**
     * Parameterized constructor to convert JSON object to JAVA object
     *
     * @param errorResponse @{@link Byte}
     */
    public Errors(byte[] errorResponse) {
        if (errorResponse != null) {
//            try {
//                JSONObject jsonObject = new JSONObject(new String(errorResponse, "UTF-8"));
//                JSONArray error = jsonObject.optJSONArray(RetrofitConstants.JSONKeys.ERRORS);
//                for (int i = 0; i < error.length(); i++) {
//                    if (TextUtils.isEmpty(errorMessage)) {
//                        errorMessage = error.getString(i);
//                    } else {
//                        errorMessage = errorMessage + "\n" + error.getString(i);
//                    }
//                }
//                errorCode = jsonObject.optString(RetrofitConstants.JSONKeys.ERROR_CODE);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }


}
