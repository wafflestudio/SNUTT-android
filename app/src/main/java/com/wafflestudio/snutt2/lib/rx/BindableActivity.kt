package com.wafflestudio.snutt2.lib.rx

import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

abstract class BindableActivity : AppCompatActivity(), RxBindable {
    private val compositeDisposable = CompositeDisposable()

    override fun bindDisposable(disposable: Disposable): Disposable {
//        TODO: lifecycle 잘 관리하기
//        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED).not())
//            throw OutOfStateException("fragment $this bind at illegal state: ${lifecycle.currentState}")

        compositeDisposable.add(disposable)
        return disposable
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
