FROM eclipse-temurin:21-jdk

RUN apt-get update && apt-get install -y python3 python3-pip && rm -rf /var/lib/apt/lists/*
RUN pip3 install --break-system-packages flask

WORKDIR /app
COPY . .

RUN javac -cp "Backend/lib/*" Backend/Login/*.java Backend/GoalService.java Backend/EventService.java

EXPOSE 8080

ENV JAVA_TOOL_OPTIONS="-Duser.timezone=UTC"

CMD ["java", "-cp", "Backend/lib/*:Backend:Backend/Login", "Main"]