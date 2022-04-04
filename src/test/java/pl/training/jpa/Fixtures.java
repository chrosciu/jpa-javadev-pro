package pl.training.jpa;

class Fixtures {

    static Client testClient() {
        var client = new Client();
        client.setFirstName("Jan");
        client.setLastName("Kowalski");
        return client;
    }

}
