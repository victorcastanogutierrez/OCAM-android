package com.ocam.proffile;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ocam.manager.UserManager;
import com.ocam.model.HikerDTO;
import com.ocam.model.UserTokenDTO;
import com.ocam.util.Constants;
import com.ocam.volley.GsonRequest;
import com.ocam.volley.VolleyManager;
import com.ocam.volley.listeners.GenericErrorListener;
import com.ocam.volley.listeners.GenericResponseListener;
import com.ocam.volley.listeners.ICommand;

public class ProffilePresenterImpl implements ProffilePresenter {

    private ProffileView proffileView;
    private VolleyManager volleyManager;

    public ProffilePresenterImpl(Context context, ProffileView proffileView) {
        this.proffileView = proffileView;
        this.volleyManager = VolleyManager.getInstance(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changePassword(String actual, String newPassword, String reNewPassword) {
        if (!assertValuesValid(actual, newPassword, reNewPassword)) {
            this.proffileView.notifyUser("Debes introducir todos los campos para cambiar la contraseña.");
            this.proffileView.hideProgress();
        } else if (!newPassword.equals(reNewPassword)) {
            this.proffileView.notifyUser("La passwords nuevas deben coincidir.");
            this.proffileView.hideProgress();
        } else {
            sendRequest(actual, newPassword);
        }
    }

    /**
     * Envía la petición POST con los datos para cambiar la password
     * @param actual
     * @param newPassword
     */
    private void sendRequest(String actual, String newPassword) {
        ICommand<UserTokenDTO> myCommand = new MyCommand();
        GsonRequest<UserTokenDTO> request = new GsonRequest<UserTokenDTO>(Constants.API_CHANGE_PASSWORD,
                Request.Method.POST, UserTokenDTO.class, null, getBodyChangePassword(actual, newPassword),
                new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

        volleyManager.addToRequestQueue(request);
    }

    /**
     * Obtiene el body de la petición en formato JSON
     * con los datos para cambiar la password
     * @param actual
     * @param newPassword
     * @return
     */
    private String getBodyChangePassword(String actual, String newPassword) {
        HikerDTO hiker = new HikerDTO();
        hiker.setUsername(UserManager.getInstance().getUserTokenDTO().getLogin());
        hiker.setPassword(actual);
        hiker.setNewPassword(newPassword);
        return new Gson().toJson(hiker);
    }

    /**
     * Comprueba el conteido de las cadenas pasadas por parámetro
     * @param actual
     * @param newPassword
     * @param reNewPassword
     * @return
     */
    private Boolean assertValuesValid(String actual, String newPassword, String reNewPassword) {
        return actual != null && newPassword != null && reNewPassword != null &&
                !actual.isEmpty() && !newPassword.isEmpty() && !reNewPassword.isEmpty();
    }

    private class MyCommand implements ICommand<UserTokenDTO> {

        @Override
        public void executeResponse(UserTokenDTO response) {
            proffileView.hideProgress();
            proffileView.notifyUserSnackbar("Password actualizada");
        }

        @Override
        public void executeError(VolleyError error) {
            proffileView.hideProgress();
            if (error  != null && error.getMessage() != null) {
                JsonObject objError = new Gson().fromJson(error.getMessage(), JsonObject.class);
                if (Constants.HTTP_422.equals(objError.get("status").getAsString())) {
                    proffileView.notifyUser(objError.get("message").getAsString());
                } else {
                    proffileView.notifyUser("Error inesperado. Prueba de nuevo más tarde");
                }
            } else {
                // No debería darse
                proffileView.notifyUser("Error inesperado. Prueba de nuevo más tarde");
            }
        }
    }
}
