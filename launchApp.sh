#!/bin/bash
set -exuo pipefail

# clean occasional previous image
docker rmi websurfer21/patentsInvestor:date || echo "image did not exist, proceeding..."

# docker build -t <docker_hub_user_name>/<image_name>:<tag_name> /path/to/dockerfile
# launch new image named "websurfer21/patentsInvestor:date"
docker build -t websurfer21/patentsInvestor:date .

# push image to dockerhub
docker push websurfer21/patentsInvestor:date

# launch new container named "patents_investor"
# -it switch allows stopping the container using Ctrl-C from the command-line
# –rm switch ensures the container is deleted once it has stopped
# docker run --rm -it -p 8000:80 --name patents_investor websurfer21/patentsInvestor:date
# run with '-d' switch (background) to allow display by terminal of next command
docker run --rm -d -p 8000:80 --name patents_investor websurfer21/patentsInvestor:date

# curl: -m, --max-time <seconds> Maximum time allowed for the transfer
curl http://localhost:8000 -s -m 10

# now, follow logs inside the container
docker logs -f patents_investor
