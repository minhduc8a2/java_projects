public class ChatBot {
    private AIModel model;

    public ChatBot(AIModel model) {
        this.model = model;
    }

    public void chat(String message) {
        System.out.println("You: " + message);
        String response = model.respond(message);
        System.out.println(response);
    }
}
