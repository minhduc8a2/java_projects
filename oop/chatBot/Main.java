public class Main {
    public static void main(String[] args) {
        ChatBot chatBot = new ChatBot(new GPTModel());
        chatBot.chat("What's up");

        ChatBot chatBot2 = new ChatBot(new BERTModel());
        chatBot2.chat("What's up");
    }
}
