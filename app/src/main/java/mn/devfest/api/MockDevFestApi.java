package mn.devfest.api;

import android.content.Context;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import mn.devfest.R;
import mn.devfest.api.model.Conference;
import retrofit.Callback;
import retrofit.client.Response;

/**
 * Provides conference info from a static JSON file.
 *
 * Currently unused. If we want to keep it, we can put it in a different build variant
 *
 * TODO Do something with this
 *
 * @author bherbst
 */
public class MockDevFestApi implements DevFestApi {
    private final Context mContext;

    public MockDevFestApi(Context context) {
        mContext = context;
    }

    @Override
    public void getConferenceInfo(Callback<Conference> callback) {
        //Reading source from local file
        InputStream inputStream = mContext.getResources().openRawResource(R.raw.firebase);
        String jsonString = readJsonFile(inputStream);


        // TODO get GSON deserializer from dagger
        Conference conference = new Conference();
        JsonParser p = new JsonParser();
        JsonObject jsonobject = p.parse(jsonString).getAsJsonObject();

        callback.success(conference, new Response("", 200, "", new ArrayList<>(0), null));
    }

    private String readJsonFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte bufferByte[] = new byte[1024];
        int length;
        try {
            while ((length = inputStream.read(bufferByte)) != -1) {
                outputStream.write(bufferByte, 0, length);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
    }
}
