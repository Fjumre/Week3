package app;

import app.endpoints.Endpoints;

public class Main {
    public static void main(String[] args) {
        // Pick port from env or default to 7007
        int port = 7007;
        String envPort = System.getenv("PORT");
        if (envPort != null) {
            try {
                port = Integer.parseInt(envPort);
            } catch (NumberFormatException ignored) {}
        }

        // Start Javalin server with your configured endpoints
        Endpoints.startServer(port);

        // log where we started
        System.out.println("Server started on http://localhost:" + port);
    }
}
