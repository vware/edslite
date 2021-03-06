package com.igeltech.nevercrypt.android.locations.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.igeltech.nevercrypt.android.Logger;
import com.igeltech.nevercrypt.android.dialogs.MasterPasswordDialog;
import com.igeltech.nevercrypt.android.dialogs.PasswordDialog;
import com.igeltech.nevercrypt.android.helpers.ActivityResultHandler;
import com.igeltech.nevercrypt.android.locations.closer.fragments.LocationCloserBaseFragment;
import com.igeltech.nevercrypt.locations.Location;
import com.igeltech.nevercrypt.locations.LocationsManager;

import java.util.ArrayList;

public class CloseLocationsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(new MainFragment(), MainFragment.TAG).commit();
    }

    public static class MainFragment extends Fragment implements LocationCloserBaseFragment.CloseLocationReceiver, MasterPasswordDialog.PasswordReceiver
    {
        public static final String TAG = "com.igeltech.nevercrypt.android.locations.activities.CloseLocationsActivity.MainFragment";
        private static final String ARG_FAILED_TO_CLOSE_ALL = "com.igeltech.nevercrypt.android.FAILED_TO_CLOSE";
        private final ActivityResultHandler _resHandler = new ActivityResultHandler();
        private ArrayList<Location> _targetLocations;
        private LocationsManager _locationsManager;
        private boolean _failedToClose;

        @Override
        public void onCreate(Bundle state)
        {
            super.onCreate(state);
            _locationsManager = LocationsManager.getLocationsManager(getActivity());
            _failedToClose = state != null && state.getBoolean(ARG_FAILED_TO_CLOSE_ALL);
            if (MasterPasswordDialog.checkMasterPasswordIsSet(getActivity(), getFragmentManager(), getTag()))
                startClosingLocations(state);
        }

        @Override
        public void onSaveInstanceState(Bundle outState)
        {
            super.onSaveInstanceState(outState);
            LocationsManager.storeLocationsInBundle(outState, _targetLocations);
            outState.putBoolean(ARG_FAILED_TO_CLOSE_ALL, _failedToClose);
        }

        @Override
        public void onTargetLocationClosed(Location location, Bundle closeTaskArgs)
        {
            if (!_targetLocations.isEmpty())
                _targetLocations.remove(0);
            closeNextLocation();
        }

        @Override
        public void onTargetLocationNotClosed(Location location, Bundle closeTaskArgs)
        {
            _failedToClose = true;
            if (!_targetLocations.isEmpty())
                _targetLocations.remove(0);
            closeNextLocation();
        }

        @Override
        public void onPause()
        {
            _resHandler.onPause();
            super.onPause();
        }

        @Override
        public void onResume()
        {
            super.onResume();
            _resHandler.handle();
        }

        private void startClosingLocations(Bundle state)
        {
            try
            {
                if (state == null)
                {
                    Intent i = getActivity().getIntent();
                    if (i != null && (i.getData() != null || i.hasExtra(LocationsManager.PARAM_LOCATION_URIS)))
                        _targetLocations = _locationsManager.getLocationsFromIntent(i);
                    else
                    {
                        _targetLocations = new ArrayList<>();
                        for (Location l : _locationsManager.getLocationsClosingOrder())
                            _targetLocations.add(l);
                    }
                }
                else
                    _targetLocations = _locationsManager.getLocationsFromBundle(state);
                closeNextLocation();
            }
            catch (Exception e)
            {
                Logger.showAndLog(getActivity(), e);
            }
        }

        private void closeNextLocation()
        {
            if (_targetLocations.isEmpty())
            {
                getActivity().setResult(_failedToClose ? AppCompatActivity.RESULT_CANCELED : AppCompatActivity.RESULT_OK);
                getActivity().finish();
            }
            else
            {
                Location loc = _targetLocations.get(0);
                String closerTag = LocationCloserBaseFragment.getCloserTag(loc);
                if (getFragmentManager().findFragmentByTag(closerTag) != null)
                    return;
                Bundle args = new Bundle();
                args.putString(LocationCloserBaseFragment.PARAM_RECEIVER_FRAGMENT_TAG, getTag());
                LocationsManager.storePathsInBundle(args, loc, null);
                Intent i = getActivity().getIntent();
                if (i.hasExtra(LocationCloserBaseFragment.ARG_FORCE_CLOSE))
                    args.putBoolean(LocationCloserBaseFragment.ARG_FORCE_CLOSE, i.getBooleanExtra(LocationCloserBaseFragment.ARG_FORCE_CLOSE, false));
                LocationCloserBaseFragment closer = LocationCloserBaseFragment.getDefaultCloserForLocation(loc);
                closer.setArguments(args);
                getFragmentManager().beginTransaction().add(closer, closerTag).commit();
            }
        }

        //master passsword is set
        @Override
        public void onPasswordEntered(PasswordDialog dlg)
        {
            startClosingLocations(null);
        }

        //master passsword is not set
        @Override
        public void onPasswordNotEntered(PasswordDialog dlg)
        {
            if (MasterPasswordDialog.checkSettingsKey(getActivity()))
                startClosingLocations(null);
            else
                getActivity().finish();
        }
    }
}
