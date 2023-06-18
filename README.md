# Android Scaffold

[Api](https://cyclops-top.github.io/scaffold/)
基于

- Navigation组件
- Kotlin协程
- Hilt

## 导航并返回结果

```kotlin
package simple

class TestFragment : Fragment(), ResultSupporter<Long> by ResultSupporter() {
    init {
        initResultCaller()
    }
    fun setResultCurrentTime() {
        setResult(System.currentTimeMillis())
    }
}

```

`nav_test.xml`

```xml

<fragment android:id="@+id/testFragment" android:name="simple.TestFragment" android:label="Test" />
```

`CallFragment`

```kotlin

fun getTestTime() {
    lifecycleScope.launch {
        val time =
            findGuardNavController().navigateForResult<Long>(R.id.testFragment) ?: return@launch
        println("get from test time -> $time")
    }
}
```

