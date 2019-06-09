package net.qiujuer.web.italker.push.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.time.LocalDateTime;


@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonProvider<T> implements MessageBodyReader<T>, MessageBodyWriter<T> {
    private static final Gson gson;

    static {

        GsonBuilder builder = new GsonBuilder()
                .serializeNulls()

                .excludeFieldsWithoutExposeAnnotation()

                .enableComplexMapKeySerialization();
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter());
        gson = builder.create();
    }


    public static Gson getGson() {
        return gson;
    }

    public GsonProvider() {
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType) {
        return true;
    }


    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations,
                      MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                      InputStream entityStream) throws IOException, WebApplicationException {
        try (JsonReader reader = new JsonReader(new InputStreamReader(entityStream, "UTF-8"))) {
            return gson.fromJson(reader, genericType);
        }
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(T t, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        try (JsonWriter jsonWriter = gson.newJsonWriter(new OutputStreamWriter(entityStream, Charset.forName("UTF-8")))) {
            gson.toJson(t, genericType, jsonWriter);
        }
    }
}