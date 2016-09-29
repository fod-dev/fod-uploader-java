package com.fortify.fod.fodapi.controllers;

import com.fortify.fod.fodapi.FodApi;

class ControllerBase {
    FodApi api;

    /**
     * Base constructor for all api controllers
     * @param api api object (containing client etc.) of controller
     */
    ControllerBase(FodApi api) { this.api = api; }
}
