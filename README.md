# Android Scaffold

[Api Doc](https://cyclops-top.github.io/scaffold/)
基于

- Navigation组件
- Kotlin协程
- Hilt

## 1.导航并返回结果

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

<fragment android:id="@+id/testFragment"
    android:name="simple.TestFragment" 
    android:label="Test" />
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

## 1. 导航前验证权限

例如需要Camera权限
`nav_test.xml`

```xml
<fragment android:id="@+id/testFragment" 
    android:name="simple.TestFragment" 
    android:label="Test" >
    <argument
        android:name="permissions"
        app:argType="string"
        android:defaultValue="android.permission.CAMERA" />
</fragment>
```
```kotlin

fun getTestTime() {
    lifecycleScope.launch {
        //同时会验证权限
        val time =
            findGuardNavController().navigateForResult<Long>(R.id.testFragment) ?: return@launch
        println("get from test time -> $time")
    }
}

//启动并不验证权限

fun launchTest(){
    findGuardNavController().navigate(R.id.testFragment)
}
```
