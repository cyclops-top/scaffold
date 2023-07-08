package scaffold.views

import android.view.LayoutInflater
import android.view.ViewGroup

typealias BindingCreator<B> = (LayoutInflater, ViewGroup?, Boolean) -> B

