package com.example.diego.prototipo.app;


//import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.GeomagneticField;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diego.prototipo.R;
import com.example.diego.prototipo.ui.PermissionUtils;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
        import com.google.android.gms.location.places.Places;
        import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;


public class Mapa extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMyLocationButtonClickListener,
   //    GoogleMap.OnPolylineClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks {


    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private LocationManager mLocationManager;

    private Ponto pontoInicial;
    private Ponto pontoFinal;


    private List<Polyline> polylines = new ArrayList<Polyline>();
    private Rota rota;

    //cores
    private static final int PRICIPAL_OK = Color.rgb(0, 0, 255);
    private static final int PRICIPAL_ATENCAO = Color.rgb(255, 153, 0);
    private static final int PRICIPAL_COMPROMETIDO = Color.rgb(255, 0, 0);

    private static final int ALTERNATIVA_OK = Color.rgb(204, 217, 255);
    private static final int ALTERNATIVA_ATENCAO = Color.rgb(255, 224, 179);
    private static final int ALTERNATIVA_COMPROMETIDO = Color.rgb(255, 204, 204);

    private FloatingActionButton infos;
    private Boolean infosClicked;

    private FloatingActionButton problem;
    private Boolean problemClicked;

    private FloatingActionButton start;
    private Boolean startClicked;

    private FloatingActionButton text;
    private Boolean textClicked;

    private Spinner marcacao;

    private float mDeclination;

    private Location mLocation;

    private int countFoto;
    private View reportar;
    private EditText descricao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_gmaps, container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment fragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        fragment.getMapAsync(this);

       SupportPlaceAutocompleteFragment autocompleteFragment = ((SupportPlaceAutocompleteFragment)
             getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment));

        Location mLastLocation = getLastKnownLocation();
        if(mLastLocation != null){

            autocompleteFragment.setBoundsBias(new LatLngBounds(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                                                                new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));

            // inserePonto(pontoInicial);

        }



        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                selecionaLocal(place);

            }

            @Override
            public void onError(Status status) {
                Log.d("V", "Ocorreu um erro: " + status);
            }
        });


        infos = (FloatingActionButton) getActivity().findViewById(R.id.infos);
        infos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                botaoInfos(view);

            }
        });
        infos.hide();
        infosClicked = false;

        problem = (FloatingActionButton) getActivity().findViewById(R.id.problem);
        problem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                botaoProblem(view);

            }
        });
        problem.hide();
        problemClicked = false;

        start = (FloatingActionButton) getActivity().findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                botaoStart(view);

            }
        });
        start.hide();
        startClicked = false;

        text = (FloatingActionButton) getActivity().findViewById(R.id.texto);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                botaoTexto(view);

            }
        });
        text.hide();
        textClicked = false;



        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                //.enableAutoManage((FragmentActivity) getActivity(), this)
                .build();

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                alteraLocalizacao(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e ){

        }

        SensorManager manager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        Sensor rotation_vector = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        SensorEventListener sensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

                    rotacionaCamera(event);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        manager.registerListener(sensorListener,rotation_vector,SensorManager.SENSOR_DELAY_NORMAL);

        countFoto = 0;
    }

    public void botaoInfos(View view){

        if (!infosClicked) {
            infosClicked = true;

            problemClicked = false;
            problem.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));

            Toast toast = Toast.makeText(getContext(), "Clique em um segmento para exibir informações de acessibilidade",Toast.LENGTH_SHORT);
            //Toast toast = Toast.makeText(getContext(), "Clique em um segmento para exibir informações de acessibilidade",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0,-400);
            toast.show();

            infos.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        }
        else{
            infosClicked = false;
            infos.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
        }
    }


    public void botaoProblem(View view){

        if (!problemClicked) {
            problemClicked = true;

            infosClicked = false;
            infos.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));

            Toast toast = Toast.makeText(getContext(), "Clique em um segmento para reportar um problema de acessibilidade",Toast.LENGTH_SHORT);
            //Toast toast = Toast.makeText(getContext(), "Click a segment to report an accessibility issue",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL , 0, -400);
            toast.show();

            problem.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        }
        else{
            problemClicked = false;
            problem.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
        }
    }


    public void botaoStart(View view){

        if (!startClicked) {
            startClicked = true;

            start.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

            infosClicked = false;
            infos.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));


            problemClicked = false;
            problem.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));


            text.show();


            Toast toast = Toast.makeText(getContext(), rota.textoRota(rota.getRotaSelecionada()).get(0),Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0,-400);
            toast.show();


        }
        else{
            startClicked = false;
            start.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));

            text.hide();

            CameraPosition pos = CameraPosition.builder(mMap.getCameraPosition()).bearing(0).tilt(0).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.style_json));

            if (!success) {
                Log.d("V", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.d("V", "Can't find style. Error: ", e);
        }




        mMap = map;

        mMap.setOnMyLocationButtonClickListener(this);
//        mMap.setOnMyLocationClickListener(this);


        //Destivar barrade ferramentas
        mMap.getUiSettings().setMapToolbarEnabled(false);

        enableMyLocation();

        Log.d("V", "Connect");
        //enableMyLocation();
        try {
            Location mLastLocation = getLastKnownLocation();
            Log.d("V", "Connect Location");
            if(mLastLocation != null){
                Log.d("V", "Latitude: " + mLastLocation.getLatitude());
                Log.d("V", "Longitude: " + mLastLocation.getLongitude());

                pontoInicial = new Ponto();
                pontoInicial.setPonto(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                pontoInicial.setNome("Ponto Inicial");

               // inserePonto(pontoInicial);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pontoInicial.getPonto(), 18.0f));

            }
        }catch (SecurityException e ){

        }

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            public void onPolylineClick(Polyline polyline) {
                polyClick(polyline);
                //   int strokeColor = polyline.getColor() ^ 0x0000CC00;
             //   polyline.setColor(strokeColor);
             //   Toast.makeText(getActivity(), "Polyline klick: " + polyline.getId(), Toast.LENGTH_LONG).show();
            }
        });

        //getActivity().getSupportFragmentManager().beginTransaction().show(R.id.place_autocomplete_fragment).commit;
      //  mMap.event.addListener(polygonObject,'dblclick',function(e){e.stop();})
    }


    public void selecionaLocal(Place place){

        Log.d("V", "Place: " + place.getName());
        if (place.getName().toString().equals("Hospital Santa Casa de Curitiba") ||
                place.getName().toString().equals("UPA Sítio Cercado")
                ) {
            pontoFinal = new Ponto();
            pontoFinal.setPonto(place.getLatLng());
            pontoFinal.setNome(place.getName().toString());

            inserePonto(pontoFinal);

            rota = geraRotaTeste(place.getName().toString());
            rota.setRotaSelecionada(1);
            insereRotas(rota, 1);
        }
        else{
            Toast toast = Toast.makeText(getContext(), "Rota não disponível no aplicativo",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0,-400);
            toast.show();

        }


    }

    public void inserePonto(Ponto p){


        mMap.addMarker(new MarkerOptions().position(p.getPonto()).title(p.getNome()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(p.getPonto(), 18.0f));
    }

    public Rota geraRotaTeste (String rota){

        Rota r = new Rota(pontoInicial, pontoFinal);

        if (rota.equals("Hospital Santa Casa de Curitiba")) {
            r.addTupla(new Tupla(new LatLng(-25.438620, -49.268929), 70, 20, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, "Avenida Sete de Setembro", "Avenida Marechal Floriano Peixoto", "esquerda"), 1);
            r.addTupla(new Tupla(new LatLng(-25.438458, -49.268423), 90, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 2, "Avenida Sete de Setembro", " ", " "), 1);
            r.addTupla(new Tupla(new LatLng(-25.438204, -49.268552), 70, 15, 0, 0, 0, 0, 0, 0, 0, 5, 0, 1, "Avenida Marechal Floriano Peixoto", "Rua Desembargador Westphalen", "esquerda"), 1);
            r.addTupla(new Tupla(new LatLng(-25.437083, -49.269105), 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, "Avenida Visconde de Guarapuava", "Rua Desembargador Westphalen", "direita"), 1);
            r.addTupla(new Tupla(new LatLng(-25.437638, -49.270524), 90, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 2, "Avenida Visconde de Guarapuava", " ", " "), 1);
            r.addTupla(new Tupla(new LatLng(-25.437442, -49.270629), 50, 22, 3, 0, 0, 0, 30, 0, 0, 5, 0, 1, "Rua Desembargador Westphalen", "Rua André de Barros", "esquerda"), 1);
            r.addTupla(new Tupla(new LatLng(-25.435914, -49.271334), 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2, "Rua Desembargador Westphalen", " ", " "), 1);
            r.addTupla(new Tupla(new LatLng(-25.435948, -49.271423), 50, 20, 30, 0, 0, 0, 0, 0, 0, 0, 3, 1, "André de Barros Street", " ", " "), 1);
            r.addTupla(new Tupla(new LatLng(-25.436353, -49.272432), 70, 0, 0, 0, 0, 0, 0, 12, 0, 0, 3, 2, "André de Barros Street", " ", " "), 1);
            r.addTupla(new Tupla(new LatLng(-25.436389, -49.272521), 50, 21, 12, 0, 0, 0, 0, 0, 0, 0, 3, 1, "André de Barros Street", "fim", " "), 1);
            r.addTupla(new Tupla(new LatLng(-25.436554, -49.272958), 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "fim", "fim", " "), 1);
            r.addTupla(new Tupla(new LatLng(-25.438620, -49.268929), 70, 20, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, "Avenida Sete de Setembro", "Rua Desembargador Westphalen", "direita"), 2);
            r.addTupla(new Tupla(new LatLng(-25.439014, -49.269867), 50, 0, 0, 0, 0, 0, 0, 30, 0, 5, 0, 2, "Avenida Sete de Setembro", " ", " "), 2);
            r.addTupla(new Tupla(new LatLng(-25.438738, -49.270004), 50, 23, 0, 0, 12, 0, 0, 0, 0, 5, 0, 1, "Rua Desembargador Westphalen", "Rua Desembargador Westphalen", " "), 2);
            r.addTupla(new Tupla(new LatLng(-25.437638, -49.270524), 90, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 2, "Avenida Visconde de Guarapuava", " ", " "), 2);
            r.addTupla(new Tupla(new LatLng(-25.437442, -49.270629), 50, 22, 3, 0, 0, 0, 30, 0, 0, 5, 0, 1, "Rua Desembargador Westphalen", "Rua André de Barros", "esquerda"), 2);
            r.addTupla(new Tupla(new LatLng(-25.435914, -49.271334), 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2, "Rua Desembargador Westphalen", " ", " "), 2);
            r.addTupla(new Tupla(new LatLng(-25.435948, -49.271423), 50, 20, 30, 0, 0, 0, 0, 0, 0, 0, 3, 1, "André de Barros Street", " ", " "), 2);
            r.addTupla(new Tupla(new LatLng(-25.436353, -49.272432), 70, 0, 0, 0, 0, 0, 0, 12, 0, 0, 3, 2, "André de Barros Street", " ", " "), 2);
            r.addTupla(new Tupla(new LatLng(-25.436389, -49.272521), 50, 21, 12, 0, 0, 0, 0, 0, 0, 0, 3, 1, "André de Barros Street", "fim", " "), 2);
            r.addTupla(new Tupla(new LatLng(-25.436554, -49.272958), 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "fim", "fim", " "), 2);
            r.addTupla(new Tupla(new LatLng(-25.438620, -49.268929), 70, 20, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, "Avenida Sete de Setembro", "Rua Desembargador Westphalen", "direita"), 3);
            r.addTupla(new Tupla(new LatLng(-25.439014, -49.269867), 70, 0, 0, 0, 0, 0, 0, 5, 0, 0, 2, 2, "Rua Desembargador Westphalen", " ", " "), 3);
            r.addTupla(new Tupla(new LatLng(-25.439073, -49.269993), 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 1, "Avenida Sete de Setembro", "Rua Alferes Poli", "direita"), 3);
            r.addTupla(new Tupla(new LatLng(-25.439809, -49.271908), 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2, "Avenida Sete de Setembro", " ", " "), 3);
            r.addTupla(new Tupla(new LatLng(-25.439523, -49.272047), 90, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 1, "Rua Alferes Poli", " ", " "), 3);
            r.addTupla(new Tupla(new LatLng(-25.438452, -49.272610), 70, 0, 0, 0, 0, 0, 0, 15, 0, 5, 0, 2, "Avenida Visconde de Guarapuava", " ", " "), 3);
            r.addTupla(new Tupla(new LatLng(-25.438249, -49.272701), 90, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 1, "Rua Alferes Poli", "Rua André de Barros", "direita"), 3);
            r.addTupla(new Tupla(new LatLng(-25.436750, -49.273389), 50, 21, 12, 0, 0, 0, 0, 0, 0, 0, 3, 1, "André de Barros Street", "fim", " "), 3);
            r.addTupla(new Tupla(new LatLng(-25.436554, -49.272958), 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "fim", "fim", " "), 3);
        }
        else if (rota.equals("UPA Sítio Cercado")) {

            r.addTupla(new Tupla(new LatLng(-25.533342,-49.270833),50,30,0,12,0,0,13,0,0,3,0,1,"Rua Paissandu","Rua Guaira", " "),1);
            r.addTupla(new Tupla(new LatLng(-25.533869,-49.270599),30,0,0,0,0,0,0,30,2,3,0,2,"Rua Guaira", " ", " "),1);
            r.addTupla(new Tupla(new LatLng(-25.533927,-49.270584),50,20,0,0,15,0,12,0,0,4,0,1,"Rua Paissandu","Rua dos Pioneiros","esquerda"),1);
            r.addTupla(new Tupla(new LatLng(-25.534572,-49.270253),70,12,0,0,0,0,10,0,0,2,0,1,"Rua dos Pioneiros","Rua Rancho Alegre"," "),1);
            r.addTupla(new Tupla(new LatLng(-25.534437,-49.269706),65,0,0,0,0,0,0,0,12,1,0,2,"Rua Rancho Alegre"," "," "),1);
            r.addTupla(new Tupla(new LatLng(-25.534424,-49.269653),85,3,0,0,0,0,1,0,0,3,0,1,"Rua dos Pioneiros","Rua Planalto"," "),1);
            r.addTupla(new Tupla(new LatLng(-25.534289,-49.269087),70,0,0,0,0,0,0,0,8,1,0,2,"Rua Planalto"," "," "),1);
            r.addTupla(new Tupla(new LatLng(-25.534277,-49.269045),83,2,0,0,0,0,0,0,0,0,1,1,"Rua dos Pioneiros","Rua Joaquim de Melo","esquerda"),1);
            r.addTupla(new Tupla(new LatLng(-25.534066,-49.268118),86,2,0,0,0,0,1,0,0,2,0,1,"Rua Joaquim de Melo","Rua José Alceu Sabatke","direita"),1);
            r.addTupla(new Tupla(new LatLng(-25.532880,-49.268576),60,0,0,0,0,0,0,0,15,0,2,2,"Rua Joaquim de Melo"," "," "),1);
            r.addTupla(new Tupla(new LatLng(-25.532859,-49.268521),75,23,2,0,2,0,0,0,0,5,0,1,"Rua Joaquim de Melo","Rua Dr Levy Buqueira","direita"),1);
            r.addTupla(new Tupla(new LatLng(-25.532347,-49.267168),82,2,0,0,2,0,1,0,0,0,2,1,"Rua Dr Levy Buqueira","fim"," "),1);
            r.addTupla(new Tupla(new LatLng(-25.533379,-49.266731),100,0,0,0,0,0,0,0,0,0,0,0,"fim","fim"," "),1);

            r.addTupla(new Tupla(new LatLng(-25.533342,-49.270833),50,30,0,12,0,0,13,0,0,3,0,1,"Rua Paissandu","Rua Guaira", "esquerda"),2);
            r.addTupla(new Tupla(new LatLng(-25.533869,-49.270599),15,23,0,11,15,0,25,0,0,8,0,1,"Rua Guaira","Rua Rancho Alegre", " "),2);
            r.addTupla(new Tupla(new LatLng(-25.533672,-49.270071),60,0,0,0,0,0,0,2,5,2,0,2,"Rua Rancho Alegre"," "," "),2);
            r.addTupla(new Tupla(new LatLng(-25.533650,-49.270021),40,25,1,15,8,5,1,0,0,2,0,1,"Rua Guaira","Rua Planalto","direita"),2);
            r.addTupla(new Tupla(new LatLng(-25.533439,-49.269492),60,0,0,0,0,0,0,1,2,2,0,2,"Rua Guaira"," "," "),2);
            r.addTupla(new Tupla(new LatLng(-25.533479,-49.269471),20,40,2,12,5,1,12,0,0,5,0,1,"Rua Planalto","Rua dos Pioneiros","esquerda"),2);
            r.addTupla(new Tupla(new LatLng(-25.534289,-49.269087),70,0,0,0,0,0,0,0,8,1,0,2,"Rua Planalto"," "," "),2);
            r.addTupla(new Tupla(new LatLng(-25.534277,-49.269045),83,2,0,0,0,0,0,0,0,0,1,1,"Rua dos Pioneiros","Rua Joaquim de Melo","esquerda"),2);
            r.addTupla(new Tupla(new LatLng(-25.534066,-49.268118),86,2,0,0,0,0,1,0,0,2,0,1,"Rua Joaquim de Melo","Rua José Alceu Sabatke","direita"),2);
            r.addTupla(new Tupla(new LatLng(-25.532880,-49.268576),60,0,0,0,0,0,0,0,15,0,2,2,"Rua Joaquim de Melo"," "," "),2);
            r.addTupla(new Tupla(new LatLng(-25.532859,-49.268521),75,23,2,0,2,0,0,0,0,5,0,1,"Rua Joaquim de Melo","Rua Dr Levy Buqueira","direita"),2);
            r.addTupla(new Tupla(new LatLng(-25.532347,-49.267168),82,2,0,0,2,0,1,0,0,0,2,1,"Rua Dr Levy Buqueira","fim"," "),2);
            r.addTupla(new Tupla(new LatLng(-25.533379,-49.266731),100,0,0,0,0,0,0,0,0,0,0,0,"fim","fim"," "),2);

            r.addTupla(new Tupla(new LatLng(-25.533342,-49.270833),50,30,0,12,0,0,13,0,0,3,0,1,"Rua Paissandu","Rua Guaira", " "),3);
            r.addTupla(new Tupla(new LatLng(-25.533869,-49.270599),30,0,0,0,0,0,0,30,2,3,0,2,"Rua Guaira", " ", " "),3);
            r.addTupla(new Tupla(new LatLng(-25.533927,-49.270584),50,20,0,0,15,0,12,0,0,4,0,1,"Rua Paissandu","Rua dos Pioneiros","esquerda"),3);
            r.addTupla(new Tupla(new LatLng(-25.534572,-49.270253),70,12,0,0,0,0,10,0,0,2,0,1,"Rua dos Pioneiros","Rua Rancho Alegre"," "),3);
            r.addTupla(new Tupla(new LatLng(-25.534437,-49.269706),65,0,0,0,0,0,0,0,12,1,0,2,"Rua Rancho Alegre"," "," "),3);
            r.addTupla(new Tupla(new LatLng(-25.534424,-49.269653),85,3,0,0,0,0,1,0,0,3,0,1,"Rua dos Pioneiros","Rua Planalto"," "),3);
            r.addTupla(new Tupla(new LatLng(-25.534289,-49.269087),70,0,0,0,0,0,0,0,8,1,0,2,"Rua Planalto"," "," "),3);
            r.addTupla(new Tupla(new LatLng(-25.534277,-49.269045),83,2,0,0,0,0,0,0,0,0,1,1,"Rua dos Pioneiros","Rua Joaquim de Melo","esquerda"),3);
            r.addTupla(new Tupla(new LatLng(-25.534066,-49.268118),86,0,0,0,0,0,0,0,5,2,0,2,"Rua Joaquim de Melo","Rua José Alceu Sabatke"," "),3);
            r.addTupla(new Tupla(new LatLng(-25.534042,-49.268058),50,12,5,0,0,0,12,0,0,2,0,1,"Rua dos Pioneiros","Rua Dr Levy Buqueira","esquerda"),3);
            r.addTupla(new Tupla(new LatLng(-25.533701,-49.266548),100,5,0,0,0,0,0,0,0,0,0,1,"Rua Dr Levy Buqueira","fim"," "),3);
            r.addTupla(new Tupla(new LatLng(-25.533379,-49.266731),100,0,0,0,0,0,0,0,0,0,0,0,"fim","fim"," "),3);



        }
        return r;
    }

    public void insereRotas (Rota r, int escolha){

        limpaMapa();

        if(escolha == 1){
            insereRota(r.getLista1(),1,true);
            insereRota(r.getLista2(),2,false);
            insereRota(r.getLista3(),3,false);
        }
        else if(escolha == 2){
            insereRota(r.getLista2(),2,true);
            insereRota(r.getLista1(),1,false);
            insereRota(r.getLista3(),3,false);
        }
        else if(escolha == 3){
            insereRota(r.getLista3(),3,true);
            insereRota(r.getLista1(),1,false);
            insereRota(r.getLista2(),2,false);
        }

        infos.show();
        problem.show();
        start.show();

    }

    public void limpaMapa(){

        for(Polyline line : polylines)
        {
            line.remove();
        }

        polylines.clear();
    }

    public void insereRota(ArrayList<Tupla> list, int id, boolean active) {


        if (active) {

            for (int i = 0; i < list.size() - 1; i++) {
                if (list.get(i).getAcess() >= 80) {
                    PolylineOptions p = new PolylineOptions();
                    p.add(list.get(i).getLatlong()).add(list.get(i + 1).getLatlong()).width(25).color(PRICIPAL_OK);

                    Polyline pl = mMap.addPolyline(p);
                    pl.setTag(String.valueOf(id) + i);

                    pl.setClickable(true);
                    pl.setZIndex((float) 10.0);

                    polylines.add(pl);

                } else if (list.get(i).getAcess() >= 50) {
                    PolylineOptions p = new PolylineOptions();
                    p.add(list.get(i).getLatlong()).add(list.get(i + 1).getLatlong()).width(25).color(PRICIPAL_ATENCAO);

                    Polyline pl = mMap.addPolyline(p);
                    pl.setTag(String.valueOf(id) + i);


                    pl.setClickable(true);
                    pl.setZIndex((float) 10.0);

                    polylines.add(pl);

                } else {
                    PolylineOptions p = new PolylineOptions();
                    p.add(list.get(i).getLatlong()).add(list.get(i + 1).getLatlong()).width(25).color(PRICIPAL_COMPROMETIDO);

                    Polyline pl = mMap.addPolyline(p);
                    pl.setTag(String.valueOf(id) + i);


                    pl.setClickable(true);
                    pl.setZIndex((float) 10.0);

                    polylines.add(pl);

                }
            }
        }
        else{

            for (int i = 0; i < list.size() - 1; i++) {
                if (list.get(i).getAcess() > 80) {

                    PolylineOptions p = new PolylineOptions();
                    p.add(list.get(i).getLatlong()).add(list.get(i + 1).getLatlong()).width(25).color(ALTERNATIVA_OK);

                    Polyline pl = mMap.addPolyline(p);
                    pl.setTag(String.valueOf(id) + i);


                    pl.setClickable(true);

                    polylines.add(pl);

                } else if (list.get(i).getAcess() > 50) {


                    PolylineOptions p = new PolylineOptions();
                    p.add(list.get(i).getLatlong()).add(list.get(i + 1).getLatlong()).width(25).color(ALTERNATIVA_ATENCAO);

                    Polyline pl = mMap.addPolyline(p);
                    pl.setTag(String.valueOf(id) + i);

                    pl.setClickable(true);
                    polylines.add(pl);

                } else {


                    PolylineOptions p = new PolylineOptions();
                    p.add(list.get(i).getLatlong()).add(list.get(i + 1).getLatlong()).width(25).color(ALTERNATIVA_COMPROMETIDO);

                    Polyline pl = mMap.addPolyline(p);
                    pl.setTag(String.valueOf(id) + i);

                    pl.setClickable(true);

                    polylines.add(pl);

                }
            }
        }
    }

    private Location getLastKnownLocation() throws SecurityException{
        mLocationManager = (LocationManager)getContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void polyClick(Polyline polyline) {


        String tag  = (String) polyline.getTag();

        int caminho  = Integer.parseInt(tag.substring(0,1));
        int index = Integer.parseInt(tag.substring(1,tag.length()));

        Log.d("V", "String encontrada: " + tag + " Caminho: " + caminho + " Indice: " + index);

        if (infosClicked){
            String text = "";

            if (caminho == 1)
                text = rota.getLista1().get(index).descricao();
            else if (caminho == 2)
                text = rota.getLista2().get(index).descricao();
            else if (caminho == 3)
                text = rota.getLista3().get(index).descricao();

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            //builder.setTitle("Informações");

            TextView title = new TextView(getActivity());
            title.setText("Informações");
            //title.setText("Information");
            title.setGravity(Gravity.CENTER);
            title.setTextSize(20);
            title.setTypeface(null, Typeface.BOLD);
            title.setTextColor(Color.BLACK);

            builder.setCustomTitle(title);

            builder.setMessage(text);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }
        else if (problemClicked) {

            reportarProblema(caminho, index);


        }
        else {
            rota.setRotaSelecionada(caminho);

            Toast toast = Toast.makeText(getContext(), "Distancia da rota selecionada: " + rota.getDistancia(caminho) + " metros",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0,-400);
            toast.show();

            insereRotas(rota, caminho);

        }
    }


    void reportarProblema(int caminho, int index){

/*        Intent intent = new Intent(getContext(), ReportarProblema.class);
        startActivity(intent);
*/
     //   ReportarProblema r = new ReportarProblema();

     //   r.show(getFragmentManager(), null);



        LayoutInflater inflater = getActivity().getLayoutInflater();

        reportar = inflater.inflate(R.layout.tela_problema, null);



        TextView segmento = (TextView) reportar.findViewById(R.id.segmento);
        String texto = "\n";
        Tupla t = null;

        if (caminho == 1) t = rota.getLista1().get(index);
        else if (caminho == 2) t = rota.getLista2().get(index);
        else if (caminho == 3) t = rota.getLista3().get(index);

        if (t.getTipo() == 1) texto = texto + " Calçada da " + t.getRua() + "\n";
        //if (t.getTipo() == 1) texto = texto  + t.getRua() + " Sidewalk\n";
        else if (t.getTipo() == 2) texto = texto + " Travessia da " + t.getRua()+ "\n";

        segmento.setText(texto);


        descricao = (EditText) reportar.findViewById(R.id.editText);

        descricao.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                Log.d("S", "Change foco" +  hasFocus);
                if (false == hasFocus) {
                    Log.d("S", "Perde foco");
                    ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            descricao.getWindowToken(), 0);
                }
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(reportar);

        TextView title = new TextView(getActivity());
        title.setText("Reportar Problema");
        //title.setText("Report an Issue");
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.BLACK);



        builder.setCustomTitle(title);

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast toast = Toast.makeText(getContext(), "Problema reportado com sucesso. Obrigado por colaborar com o Aplicativo!",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0,-400);
                        toast.show();



                        problemClicked = false;
                        problem.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));



                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        problemClicked = false;
                        problem.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));

                    }
                })
                .setNeutralButton("Foto", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }
                });

        AlertDialog dialog = builder.create();

        Spinner tipo= (Spinner) reportar.findViewById(R.id.spinnerTipo);

        ArrayList<String> s = new ArrayList<String>();
        s.add("Calçada");
        //s.add("Sidewalk");
        s.add("Rebaixamento");


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item,s);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        tipo.setAdapter(dataAdapter);





        marcacao= (Spinner) reportar.findViewById(R.id.spinnerMarcação);



        tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("V", "Clicked : " +
                        parent.getItemAtPosition(position).toString());

                tipoSelecionado(parent.getItemAtPosition(position).toString());



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                File picsDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);

                countFoto++;
                String imagem = "foto_"+countFoto+".jpg";
                File imageFile = new File(picsDir, imagem);
                LayoutInflater inflater = getActivity().getLayoutInflater();



               Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                startActivity(i);


                TextView textoFoto = (TextView) reportar.findViewById(R.id.textViewFoto);

                textoFoto.setText("    Foto: " + imagem);
                //textoFoto.setText("    Photograph: " + imagem);

                /*
                Toast toast = Toast.makeText(getContext(), "Funcionalidade não implementada",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0,-400);
                toast.show();
*/
            }
        });

    }

    public void tipoSelecionado(String selecao){

       /* LayoutInflater inflater = getActivity().getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.tela_problema, null);

        marcacao= (Spinner) promptsView.findViewById(R.id.spinnerMarcação);
*/
        ArrayList<String> l = new ArrayList<String>();
        if (selecao == "Calçada" || selecao == "Sidewalk"){
            Log.d("V", "Entrou calçada");

            l.add("Piso irregular");
            l.add("Paralelepipedo/ Petit-pavet");
            l.add("Ausência de Calçada");
            l.add("Árvores");
            l.add("Obras");
            l.add("Outros obstaculos");
            //l.add("\n" +
             //       "Other obstacles");
        }
        else if ( selecao == "Rebaixamento"){
            Log.d("V", "Entrou rebaixamento");

            l.add("Ausência de Rebaixamento");
            l.add("Problema com Rebaixamento");
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item,l);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        if(marcacao != null){
            marcacao.setAdapter(dataAdapter);
        }


    }


    private void updateCameraBearing(GoogleMap googleMap, float bearing) {
        if ( googleMap == null) return;
        CameraPosition camPos = CameraPosition
                .builder(
                        googleMap.getCameraPosition() // current Camera
                )
                .bearing(bearing)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }

    public void alteraLocalizacao(Location location){

        mLocation = location;

        GeomagneticField field = new GeomagneticField(
                (float)location.getLatitude(),
                (float)location.getLongitude(),
                (float)location.getAltitude(),
                System.currentTimeMillis()
        );

        mDeclination = field.getDeclination();

        Log.d("V", "Altera Localização");
    }

    public void rotacionaCamera(SensorEvent event){

        Log.d("V", "Rotaciona camera");
        if (startClicked) {
            float[] mRotationMatrix = new float[16];
            SensorManager.getRotationMatrixFromVector(
                    mRotationMatrix, event.values);
            float[] orientation = new float[3];
            SensorManager.getOrientation(mRotationMatrix, orientation);
            float bearing = (float) Math.toDegrees(orientation[0]) + mDeclination;

            Log.d("V", "Bearing: " + bearing);

            CameraPosition pos = new CameraPosition(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()),18.0f, 30, bearing);//mMap.getCameraPosition();


            //CameraPosition pos = CameraPosition.builder(pos).bearing(bearing).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
        }
    }

    void botaoTexto(View v){


        Log.d("S", "Botão texto Caminho: " + rota.getRotaSelecionada());

        if (!textClicked) {
            textClicked = true;

            text.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));


            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            //builder.setTitle("Informações");

            TextView title = new TextView(getActivity());
            title.setText("Caminho");
            title.setGravity(Gravity.CENTER);
            title.setTextSize(20);
            title.setTypeface(null, Typeface.BOLD);
            title.setTextColor(Color.BLACK);

            builder.setCustomTitle(title);

            String s = "";
            ArrayList<String> l = rota.textoRota(rota.getRotaSelecionada());

            for (String n: l
                 ) {
                s = s + n + "\n\n";

            }


            builder.setMessage(s);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {

                    textClicked = false;
                    text.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();


        }
        else{
            textClicked = false;
            text.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));


        }
    }

}
