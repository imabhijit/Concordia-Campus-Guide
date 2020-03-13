package com.example.concordia_campus_guide.Activities;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.concordia_campus_guide.Database.AppDatabase;
import com.example.concordia_campus_guide.Fragments.InfoCardFragment.InfoCardFragment;
import com.example.concordia_campus_guide.Global.ApplicationState;
import com.example.concordia_campus_guide.Models.Building;
import com.example.concordia_campus_guide.Models.Buildings;
import com.example.concordia_campus_guide.Models.Floors;
import com.example.concordia_campus_guide.Models.Relations.BuildingWithFloors;
import com.example.concordia_campus_guide.Models.Relations.FloorWithRooms;
import com.example.concordia_campus_guide.Models.Rooms;
import com.example.concordia_campus_guide.Models.WalkingPoint;
import com.example.concordia_campus_guide.Models.WalkingPoints;
import com.example.concordia_campus_guide.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;
    InfoCardFragment infoCardFragment;
    MainActivityViewModel mViewModel;
    private BottomSheetBehavior swipeableInfoCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        fragmentManager = getSupportFragmentManager();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        MainActivity.this.setTitle("ConUMaps");

        View infoCard = findViewById(R.id.info_card);
        swipeableInfoCard = BottomSheetBehavior.from(infoCard);

        setUpDb();

        //examples on how to get objects from db
        //begin
        AppDatabase appDB = AppDatabase.getInstance(this);
        List<Building> listBuildings = appDB.buildingDao().getAll();
        List<BuildingWithFloors> buildingWithFloorsList = appDB.buildingDao().getBuildingsWithFloors();
        List<FloorWithRooms> floorWithRooms = appDB.floorDao().getFloorWithRooms();
        List<WalkingPoint> walkingPoints = appDB.walkingPointDao().getAll();
        //end
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.search){
            //TODO onClick of search_activity button
            Intent openSearch = new Intent(MainActivity.this,
                    SearchActivity.class);

            startActivity(openSearch);
            return false;
        }

        return true;
    }

    /**
     * Show the info card fragment in the view
     *
     * @param buildingCode: the Building code
     */
    public void showInfoCard(String buildingCode){
        if(infoCardFragment!=null){
            hideInfoCard();
        }

        infoCardFragment = new InfoCardFragment();
        infoCardFragment.setBuildingCode(buildingCode);

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.info_card_frame, infoCardFragment);
        fragmentTransaction.commit();

        swipeableInfoCard.setState(BottomSheetBehavior.STATE_COLLAPSED);

    }

    /**
     * Hides the info card fragment from the view.
     */
    public void hideInfoCard(){
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(infoCardFragment);
        fragmentTransaction.commit();
    }

    /**
     * Defines the desired behavior on backpress
     */
    @Override
    public void onBackPressed(){
        Fragment fragment = fragmentManager.findFragmentById(R.id.info_card_frame);
        if(fragment!=null){
            hideInfoCard();
        }
        else{
            super.onBackPressed();
        }
    }

    private void setUpDb(){
        //delete previous db
        getApplication().getApplicationContext().deleteDatabase(AppDatabase.DB_NAME);

        //load buildings
        Buildings buildings = ApplicationState.getInstance(this).getBuildings();
        AppDatabase appDb = AppDatabase.getInstance(this);
        appDb.buildingDao().insertAll(buildings.getBuildings());

        //load floors
        Floors floors = ApplicationState.getInstance(this).getFloors();
        appDb.floorDao().insertAll(floors.getFloors());

        //load rooms
        Rooms rooms = ApplicationState.getInstance(this).getRooms();
        appDb.roomDao().insertAll(rooms.getRooms());

        WalkingPoints walkingPoints = ApplicationState.getInstance(this).getWalkingPoints();
        appDb.walkingPointDao().insertAll(walkingPoints.getWalkingPoints());
    }
}