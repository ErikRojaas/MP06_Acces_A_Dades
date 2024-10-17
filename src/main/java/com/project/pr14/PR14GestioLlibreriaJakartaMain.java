package com.project.pr14;

import jakarta.json.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.project.objectes.Llibre;

public class PR14GestioLlibreriaJakartaMain {

    private final File dataFile;

    public PR14GestioLlibreriaJakartaMain(File dataFile) {
        this.dataFile = dataFile;
    }

    public static void main(String[] args) {
        // Fitxer d'entrada "llibres_input.json" dins de la carpeta "data/pr14"
        File dataFile = new File(System.getProperty("user.dir"), "data/pr14" + File.separator + "llibres_input.json");
        PR14GestioLlibreriaJakartaMain app = new PR14GestioLlibreriaJakartaMain(dataFile);
        app.processarFitxer();
    }

    public void processarFitxer() {
        List<Llibre> llibres = carregarLlibres();
        if (llibres != null) {
            modificarAnyPublicacio(llibres, 1, 1995);
            afegirNouLlibre(llibres, new Llibre(4, "Històries de la ciutat", "Miquel Soler", 2022));
            esborrarLlibre(llibres, 2);
            guardarLlibres(llibres);
        }
    }

    public List<Llibre> carregarLlibres() {
        List<Llibre> llibres = new ArrayList<>();
        try (JsonReader reader = Json.createReader(Files.newBufferedReader(dataFile.toPath()))) {
            JsonArray jsonArray = reader.readArray();
            for (JsonValue jsonValue : jsonArray) {
                JsonObject jsonObject = jsonValue.asJsonObject();
                int id = jsonObject.getInt("id");
                String titol = jsonObject.getString("titol");
                String autor = jsonObject.getString("autor");
                int any = jsonObject.getInt("any");
                llibres.add(new Llibre(id, titol, autor, any));
            }
        } catch (IOException e) {
            System.err.println("Error llegint el fitxer JSON: " + e.getMessage());
            return null;
        }
        return llibres;
    }

    public void modificarAnyPublicacio(List<Llibre> llibres, int id, int nouAny) {
        for (Llibre llibre : llibres) {
            if (llibre.getId() == id) {
                llibre.setAny(nouAny);
                break;
            }
        }
    }

    public void afegirNouLlibre(List<Llibre> llibres, Llibre nouLlibre) {
        llibres.add(nouLlibre);
    }

    public void esborrarLlibre(List<Llibre> llibres, int id) {
        llibres.removeIf(llibre -> llibre.getId() == id);
    }

    public void guardarLlibres(List<Llibre> llibres) {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (Llibre llibre : llibres) {
            JsonObject llibreJson = Json.createObjectBuilder()
                    .add("id", llibre.getId())
                    .add("titol", llibre.getTitol())
                    .add("autor", llibre.getAutor())
                    .add("any", llibre.getAny())
                    .build();
            jsonArrayBuilder.add(llibreJson);
        }

        // Guardar el fitxer de sortida a la mateixa carpeta "data/pr14"
        File outputFile = new File(dataFile.getParent(), "llibres_output_jakarta.json");
        try (JsonWriter writer = Json.createWriter(new FileWriter(outputFile))) {
            JsonArray jsonArray = jsonArrayBuilder.build();
            writer.writeArray(jsonArray);
        } catch (IOException e) {
            System.err.println("Error escrivint el fitxer JSON: " + e.getMessage());
        }
    }
}
