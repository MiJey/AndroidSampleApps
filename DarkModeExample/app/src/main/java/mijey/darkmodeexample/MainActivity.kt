package mijey.darkmodeexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        light_theme_button.setOnClickListener {
            ThemeManager.applyTheme(ThemeManager.ThemeMode.LIGHT)
        }

        dark_theme_button.setOnClickListener {
            ThemeManager.applyTheme(ThemeManager.ThemeMode.DARK)
        }

        default_theme_button.setOnClickListener {
            ThemeManager.applyTheme(ThemeManager.ThemeMode.DEFAULT)
        }
    }
}
