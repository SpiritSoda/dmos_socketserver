package com.dmos.dmos_socketserver.dmos_common.data;

import lombok.Data;

@Data
public class ClientConfigDTO {
    private int id;
    private int interval;
    private String ip;
}
