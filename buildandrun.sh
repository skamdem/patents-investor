#!/bin/bash
set -exuo pipefail

# run [ $ ./buildandrun.sh ] to execute this script in bash shell

# read the Dockerfile and containerize the application
docker build -t websurfer21/patentsinvestorimage .

# docker push websurfer21/patentsinvestorimage
docker run --name patentsinvestorcontainer --rm -it -p 8080:8080 websurfer21/patentsinvestorimage

# clean previous image
docker rmi websurfer21/patentsinvestorimage