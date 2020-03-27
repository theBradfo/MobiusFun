package com.thebradfo.mobius;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.spotify.mobius.Connection;
import com.spotify.mobius.Mobius;
import com.spotify.mobius.MobiusLoop;
import com.spotify.mobius.Next;
import com.spotify.mobius.functions.Consumer;

import timber.log.Timber;

import static com.spotify.mobius.Effects.effects;
import static com.spotify.mobius.Next.dispatch;
import static com.spotify.mobius.Next.next;
import static com.thebradfo.mobius.Effect.REPORT_ERROR_NEGATIVE;

public class MainActivity extends AppCompatActivity {
    private TextView counterView;
    private MobiusLoop<Integer, Event, ?> loop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loop = Mobius.loop(this::update, this::effectHandler)
                        .startFrom(2);

        counterView = findViewById(R.id.timer_view);

        findViewById(R.id.up).setOnClickListener(view -> loop.dispatchEvent(Event.UP));
        findViewById(R.id.down).setOnClickListener(view -> loop.dispatchEvent(Event.DOWN));
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        loop.observe(counter -> counterView.setText(counter.toString()));
    }

    @Override
    protected void onPause() {
        super.onPause();

        loop.dispose();
    }

    private void showError() {
        counterView.post(() -> Toast.makeText(MainActivity.this, "Negative Number!", Toast.LENGTH_SHORT).show());
    }

    @NonNull
    Next<Integer, Effect> update(int model, Event event) {
        switch (event) {
            case UP:
                return next(model + 1);

            case DOWN:
                if (model > 0) {
                    return next(model - 1);
                }
                Timber.d("Dispatching effect!");
                return dispatch(effects(REPORT_ERROR_NEGATIVE));

            default:
                throw new IllegalArgumentException("cannot handle " + event);
        }
    }

    @NonNull
    Connection<Effect> effectHandler(Consumer<Event> eventConsumer) {
        return new Connection<Effect>() {
            @Override
            public void accept(@NonNull Effect effect) {
                Timber.i("Processing effect %s", effect);
                if (effect == REPORT_ERROR_NEGATIVE) {
                    showError();
                } else {
                    throw new IllegalArgumentException("Unable to handle " + effect);
                }
            }

            @Override
            public void dispose() { }
        };
    }
}
