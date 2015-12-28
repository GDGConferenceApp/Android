package mn.devfest.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import mn.devfest.R;
import mn.devfest.api.model.Conference;

/**
 * Created by chris.black on 12/5/15.
 */
public class DevFestDataSource {

    public DevFestDataSource(Context context) {
        //Reading source from local file
        InputStream inputStream = context.getResources().openRawResource(R.raw.conference);
        String jsonString = readJsonFile(inputStream);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();
        Conference conference = gson.fromJson(jsonString, Conference.class);
        System.out.println(conference.toString());
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
