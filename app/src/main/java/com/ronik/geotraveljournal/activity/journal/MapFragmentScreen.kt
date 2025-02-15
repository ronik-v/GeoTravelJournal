import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.ronik.geotraveljournal.activity.MapFragment

@Composable
fun MapFragmentScreen(
    navController: NavController,
    routePoints: String? = null
) {
    AndroidView(
        factory = { context ->
            FrameLayout(context).apply {
                id = View.generateViewId()
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            }
        },
        update = { container ->
            val activity = container.context as FragmentActivity
            if (activity.supportFragmentManager.findFragmentById(container.id) == null) {
                container.post {
                    activity.supportFragmentManager.beginTransaction()
                        .replace(
                            container.id,
                            MapFragment().apply {
                                arguments = Bundle().apply {
                                    putString("routePoints", routePoints)
                                }
                            }
                        )
                        .commit()
                }
            }
        }
    )
}
