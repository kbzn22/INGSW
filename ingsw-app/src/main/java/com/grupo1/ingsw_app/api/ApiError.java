package com.grupo1.ingsw_app.api;

import java.time.OffsetDateTime;

public record ApiError(
            OffsetDateTime timestamp,
            int status,
            String error,
            String message,
            String path,
            String code
) {}


