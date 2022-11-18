package com.dmos.dmos_socketserver.dmos_common.data;

import lombok.Data;

import java.util.HashMap;

@Data
public class DMOSResponse {
    private int code;
    private HashMap<String, String> data;
}