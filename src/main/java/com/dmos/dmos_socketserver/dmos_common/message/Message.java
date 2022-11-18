package com.dmos.dmos_socketserver.dmos_common.message;

import lombok.Data;

import java.util.HashMap;

@Data
public class Message {
    private int type;
    private String data;
}
