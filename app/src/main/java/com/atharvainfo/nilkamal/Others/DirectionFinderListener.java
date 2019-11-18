package com.atharvainfo.nilkamal.Others;

import java.util.List;
import com.atharvainfo.nilkamal.Others.Route;


public interface DirectionFinderListener {
    void onDirectionFinderStart();

    void onDirectionFinderSuccess(List<Route> route);
}
