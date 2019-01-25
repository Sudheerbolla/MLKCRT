
package com.godavarisandroid.mystore.interfaces;

public interface IParseListener {

    void ErrorResponse(String response, int requestCode);

    void SuccessResponse(String response, int requestCode);
}
