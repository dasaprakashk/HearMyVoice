package hearmyvoice.com.hearmyvoice;

import android.app.Application;
import com.parse.Parse;

/**
 * Created by anu on 09-05-2015.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "zgU4noCkdaH0Pvs2T6iKAVflzlMctBgPQIAvS5DE", "sSwvCZZfxb97v607PFdvbAiUoG3L7QQDT9HE4gQM");
        //Parse.enableLocalDatastore(this);
    }
}
