#!/usr/bin/env bash

fuser $E_ARB_DEV_CLIENT_PORT/tcp -k &
fuser $E_ARB_DEV_HYS_PORT/tcp -k &
fuser $E_ARB_KIB_PORT/tcp -k &
fuser $E_ARB_ES_PORT/tcp -k &
fuser $E_ARB_LOG_PORT/tcp -k &
fuser $E_ARB_DEV_API_PORT/tcp -k &
fuser $E_ARB_DEV_AUTH_PORT/tcp -k &
fuser $E_ARB_DEV_EXEC_PORT/tcp -k &
fuser $E_ARB_DEV_TOUR_PORT/tcp -k &
fuser $E_ARB_DEV_TRES_PORT/tcp -k &
fuser $E_ARB_DEV_EUR_PORT/tcp -k &
fuser $E_ARB_CFG_PORT/tcp -k