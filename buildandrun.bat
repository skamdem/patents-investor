:: run [ $ buildandrun.bat ] to execute this script in command prompt
docker build -t websurfer21/patentsinvestorimage .
:: docker push websurfer21/patentsinvestorimage
docker run --name patentsinvestorcontainer --rm -it -p 8080:8080 websurfer21/patentsinvestorimage
docker rmi websurfer21/patentsinvestorimage