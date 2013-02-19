LoadersAndFragments
===================

This project demonstrates how a call to getTransactionManager...commit() from an onLoadFinished callback blows[1] up with an IllegalStateException.  This is correct behaviour as documented in the [API] [2].

The question is, however, what is best practice for addressing this requirement?  It is possible to use 'commitAllowingStateLoss' _but_ I have not seen simple documentation covering the practical effects from that.  Alternatively, one might design out the fragment replacement by fetching the fragment (possibly from a member variable) and directly access the fragment methods (rather than using arguments).
 
There are [examples][3] of workarounds using handlers within onLoadFinished but there is a question of whether that is simply hiding the issues?  Is that fundamentally the same as commitAllowingStateLoss?
Dianne has also passed some [comments][4].

The code of interest is as follows within ConstantsBrowser.  Replacing commit with commitAllowingStateLoss is the most simple fix.

```
  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    adapter.swapCursor(cursor);
    
    Fragment fragment = new DetailsFragment();
    Bundle args = new Bundle();
    args.putString(DetailsFragment.KEY_DETAILS, "Total items: " + cursor.getCount());
    fragment.setArguments(args);
    
    // This will cause an exception
    getFragmentManager().beginTransaction().replace(R.id.main_details, fragment).commit();
```    
    
[1]: Exception from getFragmentManager().beginTransaction().replace(R.id.main_details, fragment).commit();
```
02-19 20:46:18.509: ERROR/AndroidRuntime(32431): FATAL EXCEPTION: main
        java.lang.IllegalStateException: Can not perform this action inside of onLoadFinished
        at android.app.FragmentManagerImpl.checkStateLoss(FragmentManager.java:1322)
        at android.app.FragmentManagerImpl.enqueueAction(FragmentManager.java:1329)
        at android.app.BackStackRecord.commitInternal(BackStackRecord.java:595)
        at android.app.BackStackRecord.commit(BackStackRecord.java:574)
        at com.commonsware.android.loader.ConstantsBrowser.onLoadFinished(ConstantsBrowser.java:118)
        at com.commonsware.android.loader.ConstantsBrowser.onLoadFinished(ConstantsBrowser.java:36)
        at android.app.LoaderManagerImpl$LoaderInfo.callOnLoadFinished(LoaderManager.java:483)
        at android.app.LoaderManagerImpl$LoaderInfo.onLoadComplete(LoaderManager.java:451)
        at android.content.Loader.deliverResult(Loader.java:143)
        at android.content.CursorLoader.deliverResult(CursorLoader.java:113)
        at android.content.CursorLoader.deliverResult(CursorLoader.java:43)
        at android.content.AsyncTaskLoader.dispatchOnLoadComplete(AsyncTaskLoader.java:254)
        at android.content.AsyncTaskLoader$LoadTask.onPostExecute(AsyncTaskLoader.java:91)
        at android.os.AsyncTask.finish(AsyncTask.java:631)
        at android.os.AsyncTask.access$600(AsyncTask.java:177)
        at android.os.AsyncTask$InternalHandler.handleMessage(AsyncTask.java:644)
        at android.os.Handler.dispatchMessage(Handler.java:99)
        at android.os.Looper.loop(Looper.java:137)
        at android.app.ActivityThread.main(ActivityThread.java:5193)
        at java.lang.reflect.Method.invokeNative(Native Method)
        at java.lang.reflect.Method.invoke(Method.java:511)
        at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:795)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:562)
        at dalvik.system.NativeStart.main(Native Method)
```
[2]: https://developer.android.com/reference/android/app/LoaderManager.LoaderCallbacks.html#onLoadFinished(android.content.Loader<D>,%20D)
[3]: http://stackoverflow.com/questions/7746140/android-problems-using-fragmentactivity-loader-to-update-fragmentstatepagera
[4]: https://groups.google.com/forum/?fromgroups=#!topic/android-developers/dXZZjhRjkMk
