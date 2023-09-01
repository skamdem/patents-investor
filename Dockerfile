# Use an image with the SDK for compilation
FROM openjdk:8-jdk-alpine AS builder

LABEL author="Sylvain K."
LABEL "dev.skamdem.patentsinvestor"="Patents Investor App."
LABEL version="1.0"
LABEL description="Investing for everyone, \
from the Cash-strapped to the deep-pocketed."

WORKDIR /out

# Get the source code inside the image
COPY /src/main .

# Compile source code
RUN javac java/dev/skamdem/patentsinvestor/PatentsInvestorApplication.java

# Create a lightweight image
FROM openjdk:8-jre-alpine

# Copy compiled artifacts from previous image
COPY --from=builder /out/*.class .
EXPOSE 8080
CMD ["java", "PatentsInvestorApplication"]
