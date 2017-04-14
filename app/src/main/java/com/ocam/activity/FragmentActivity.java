package com.ocam.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ocam.R;
import com.ocam.activity.monitorization.MonitorizationActivity;
import com.ocam.activity.track.TrackActivity;
import com.ocam.manager.App;
import com.ocam.manager.UserManager;
import com.ocam.model.Activity;
import com.ocam.model.DaoSession;
import com.ocam.model.types.ActivityStatus;
import com.ocam.periodicTasks.GPSLocation;
import com.ocam.periodicTasks.ReportSender;
import com.ocam.periodicTasks.state.DisconnectedState;
import com.ocam.util.ConnectionUtil;
import com.ocam.util.Constants;
import com.ocam.util.PreferencesUtils;
import com.ocam.util.ViewUtils;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.ALARM_SERVICE;
import static com.ocam.periodicTasks.GPSLocation.checkPermission;
import static com.ocam.util.DateUtils.formatDate;

/**
 * Fragment para la vista del detalle de una actividad
 */
public class FragmentActivity extends Fragment implements ActivityView {

    private TextView txDescripcion;
    private TextView txFecha;
    private TextView txOrganiza;
    private TextView txDetalle;
    private TextView txMide;
    private TextView txPlazas;
    private TextView txEstado;
    private Activity activity;
    private ActivityPresenter activityPresenter;
    private LinearLayout guiasLayout;
    private Button btComenzar;
    private Button btMonitorizar;
    private Button btCambiarPassword;
    private Button btUnirse;
    private Button btCerrar;
    private ProgressBar mProgress;
    private Dialog mOverlayDialog;
    private EditText input; // Dialog de password

    public FragmentActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_activity, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_detail, container, false);
        this.activityPresenter = new ActivityPresenterImpl(this, v.getContext());
        this.txDescripcion = (TextView) v.findViewById(R.id.lbDescripcion);
        this.txFecha = (TextView) v.findViewById(R.id.lbFecha);
        this.txOrganiza = (TextView) v.findViewById(R.id.lbOrganiza);
        this.txDetalle = (TextView) v.findViewById(R.id.lbDetalle);
        this.txMide = (TextView) v.findViewById(R.id.lbMide);
        this.txPlazas = (TextView) v.findViewById(R.id.lbPlazas);
        this.txEstado = (TextView) v.findViewById(R.id.lbEstado);
        this.guiasLayout = (LinearLayout) v.findViewById(R.id.linearGuia);
        this.btComenzar = (Button) v.findViewById(R.id.btComenzar);
        this.btMonitorizar = (Button) v.findViewById(R.id.btMonitorizar);
        this.btCambiarPassword = (Button) v.findViewById(R.id.btCambiarPassword);
        this.btUnirse = (Button) v.findViewById(R.id.btUnirse);
        this.btCerrar = (Button) v.findViewById(R.id.btCerrar);
        this.mProgress = (ProgressBar) v.findViewById(R.id.progressBar);
        this.mOverlayDialog = new Dialog(v.getContext(), android.R.style.Theme_Panel);
        Bundle args = getArguments();
        setUpActivityData(new Gson().fromJson(args.getString("activity"), Activity.class));

        btCambiarPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pw = input.getText().toString();
                        if (assertPasswordValid(pw)) {
                            activity.setPassword(input.getText().toString());
                            activityPresenter.updatePasswordActivity(activity.getId(), input.getText().toString());
                        } else {
                            notifyUser("Debes introducir una password (entre 3 y 12 caracteres)");
                        }
                    }
                });
            }
        });

        btComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pw = input.getText().toString();
                        if (assertPasswordValid(pw)) {
                            activity.setPassword(input.getText().toString());
                            activityPresenter.startActivity(activity.getId(), input.getText().toString());
                        } else {
                            notifyUser("Debes introducir una password (entre 3 y 12 caracteres)");
                        }
                    }
                });
            }
        });

        btUnirse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmJoinDialog();
            }
        });


        btMonitorizar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                iniciarMonitorizationFragment();
            }
        });

        btCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmCerrarDialog();
            }
        });


        Button prueba = (Button) v.findViewById(R.id.prueba);
        prueba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!GPSLocation.checkPermission(getContext())) {
                    requestPermissions(
                            new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION},
                            123);
                }

                DaoSession daoSession = ((App) getContext().getApplicationContext()).getDaoSession();
                daoSession.getReportDao().deleteAll();
                daoSession.getGPSPointDao().deleteAll();

                Intent intent = new Intent(getContext(), ReportSender.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), Constants.BROADCAST_INTENT, intent, 0);
                AlarmManager alarm = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, 60000, pendingIntent);
                Log.d("ReportSender", "Comienza proceso");
            }
        });

        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getContext(), ReportSender.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), Constants.BROADCAST_INTENT, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pendingIntent);
                    Log.d("REPORTE", "Inicia proceso");

                    NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notification = new NotificationCompat.Builder(getContext())
                            .setContentTitle("Participas en una actividad en curso")
                            .setContentText("Aún no se ha enviado ningún reporte")
                            .setOngoing(true)
                            .setSmallIcon(R.drawable.mountain_home)
                            .build();

                    notificationManager.notify(Constants.ONGOING_NOTIFICATION_ID, notification);
                } else {
                    ViewUtils.showToast(getContext(), Toast.LENGTH_LONG, "Sin los permisos necesarios no podemos iniciar la actividad");
                }
                return;
            }
        }
    }

    /**
     * Inicia el fragment de la monitorización pasando la ID de la actividad
     * por parámetro
     */
    private void iniciarMonitorizationFragment() {
        Intent i = new Intent(getActivity(), MonitorizationActivity.class);
        i.putExtra("activityId", activity.getId());
        startActivity(i);
    }

    /**
     * Expone los datos de la actividad en los label correspondientes
     * @param activity
     */
    private void setUpActivityData(Activity activity) {
        this.activity = activity;
        this.txEstado.setText(activity.getFormattedStatus());
        this.txDescripcion.setText(activity.getShortDescription());
        this.txFecha.setText(formatDate(activity.getStartDate()));
        this.txOrganiza.setText(activity.getOwner().getEmail());
        if (activity.getLongDescription() != null) {
            this.txDetalle.setText(activity.getLongDescription());
            this.txDetalle.setVisibility(View.VISIBLE);
        }

        if (activity.getMide() != null) {
            this.txMide.setText("Enlace a detalle: "+activity.getMide());
            this.txMide.setVisibility(View.VISIBLE);
        }

        if (activity.getMaxPlaces() != null) {
            this.txPlazas.setText(activity.getMaxPlaces().toString()+" plazas máximas");
            this.txPlazas.setVisibility(View.VISIBLE);
        }

        //Si es guía
        if (this.activityPresenter.isUserGuide(this.activity)) {
            this.guiasLayout.setVisibility(View.VISIBLE);
            if (!this.activityPresenter.assertActivityRunning(activity)) {
                this.btComenzar.setVisibility(View.VISIBLE);
            } else {
                //Siendo guía y estando activa, la puede cerrar o cambiar la password
                this.btCambiarPassword.setVisibility(View.VISIBLE);
                this.btCerrar.setVisibility(View.VISIBLE);
            }
        }

        //Actividad abierta y guía o participante
        if (this.activityPresenter.puedeMonitorizar(this.activity)) {
            this.btMonitorizar.setEnabled(Boolean.TRUE);
        } else {
            this.btMonitorizar.setVisibility(View.GONE);
        }

        if (this.activityPresenter.puedeUnirse(this.activity)) {
            this.btUnirse.setVisibility(View.VISIBLE);
        } else {
            this.btUnirse.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_track:
                mostrarTrack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Método onClick para mostrar la actividad con el track de la ruta
     */
    public void mostrarTrack() {
        Intent i = new Intent(getContext(), TrackActivity.class);
        i.putExtra("activityId", this.activity.getId());
        startActivity(i);
    }

    /**
     * Método que muestra el dialog de confirmacion de iniciar actividad
     * con la solicitud de la password al usuario
     */
    private void showPasswordDialog(DialogInterface.OnClickListener aceptarCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
        builder.setTitle("Introduce una password para la actividad");

        this.input = new EditText(getView().getContext());
        this.input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(this.input);

        builder.setPositiveButton("Aceptar", aceptarCallback);
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private Boolean assertPasswordValid(String password) {
        return password != null && !password.isEmpty() && password.length() >= 3 && password.length() <= 12;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayProgress() {
        mOverlayDialog.setCancelable(false);
        mOverlayDialog.show();
        this.mProgress.setVisibility(View.VISIBLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideProgress() {
        mOverlayDialog.cancel();
        this.mProgress.setVisibility(View.GONE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityOpen() {
        this.activity.setStatus(ActivityStatus.RUNNING);
        this.btComenzar.setVisibility(View.GONE);
        btMonitorizar.setEnabled(Boolean.TRUE);
        btMonitorizar.setVisibility(View.VISIBLE);
        btCambiarPassword.setVisibility(View.VISIBLE);
        this.txEstado.setText(this.activity.getFormattedStatus());
        Snackbar.make(getView(), "Actividad abierta. Password: "+this.activity.getPassword(), Snackbar.LENGTH_LONG)
            .setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            })
            .setActionTextColor(getResources().getColor(android.R.color.holo_green_light ))
            .show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onHikerJoinActivity() {
        this.btUnirse.setVisibility(View.GONE);
        this.btMonitorizar.setVisibility(View.VISIBLE);
        this.btMonitorizar.setEnabled(Boolean.TRUE);
        iniciarMonitorizationFragment();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityClosed() {
        Snackbar.make(getView(), "Actividad cerrada", Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_green_light ))
                .show();

        activity.setStatus(ActivityStatus.CLOSED);
        btCerrar.setVisibility(View.GONE);
        btMonitorizar.setVisibility(View.GONE);
        btCambiarPassword.setVisibility(View.GONE);
        txEstado.setText(activity.getFormattedStatus());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyUser(String notificationText) {
        ViewUtils.showToast(getView().getContext(), Toast.LENGTH_SHORT, notificationText);
    }

    /**
     * Confirmación visual de unirse a una actividad
     */
    private void showConfirmJoinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
        builder.setTitle(R.string.app_name);
        builder.setTitle("Monitorización actividad");
        builder.setMessage("Al unirte a la actividad y mientras esté en curso, enviaremos tu posición cada cierto tiempo para mantenerte monitorizado. También podrás ver la posición del resto del grupo.");
        builder.setPositiveButton("Unirme", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                activityPresenter.joinActivity(activity);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Confirmación visual para cerrar una actividad
     */
    private void showConfirmCerrarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
        builder.setTitle(R.string.app_name);
        builder.setTitle("Concluir actividad");
        builder.setMessage("Al dar por concluída la actividad se parará la monitorización y no se podrá volver a abrir");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                activityPresenter.closeActivity(activity);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
