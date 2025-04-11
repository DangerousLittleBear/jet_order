#!/bin/bash

# Start MySQL
docker-entrypoint.sh mysqld &

# Start Scouter Agent if enabled
if [ "$SCOUTER_AGENT_ENABLED" = "true" ]; then
    cd /opt/scouter-agent
    ./host.sh &
fi

# Wait for any process to exit
wait -n

# Exit with status of process that exited first
exit $?
