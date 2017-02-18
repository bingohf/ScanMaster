package com.ledway.scanmaster;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ledway.scanmaster.data.DBCommand;
import com.ledway.scanmaster.data.Settings;
import com.ledway.scanmaster.ui.AppPreferences;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
  private static final int REQUEST_SET = 1;
  @Inject Settings settings;
  @BindView(R.id.txt_bill_no) EditText mTxtBill;
  @BindView(R.id.txt_barcode) EditText mTxtBarcode;
  @BindView(R.id.prg_loading) View mLoading;
  @BindView(R.id.txt_response) TextView mTxtResponse;
  private DBCommand dbCommand = new DBCommand();
  private CompositeSubscription mSubscriptions = new CompositeSubscription();
  private EditText mCurrEdit;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    ((MApp) getApplication()).getAppComponet().inject(this);
    settingChanged();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings: {
        startActivityForResult(new Intent(this, AppPreferences.class), REQUEST_SET);
        break;
      }
    }
    return true;
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
    if (result != null) {
      if (result.getContents() != null) {
        String code = result.getContents();
        receiveCode(code);
      }
    } else {
      switch (requestCode) {
        case REQUEST_SET: {
          settingChanged();
          break;
        }
      }
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  private void receiveCode(String code) {
    if (mCurrEdit != null) {
      mCurrEdit.setText(code);
      if (mCurrEdit.getId() == R.id.txt_bill_no) {
        queryBill();
      } else if (mCurrEdit.getId() == R.id.txt_barcode) queryBarCode();
    }
  }

  private void settingChanged() {
    String connectionStr =
        String.format("jdbc:jtds:sqlserver://%s;DatabaseName=%s;charset=UTF8", settings.getServer(),
            settings.getDb());
    Timber.v(connectionStr);
    dbCommand.setConnectionString(connectionStr);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mSubscriptions.clear();
  }

  @OnClick(R.id.btn_camera_scan_bill) void onBillCameraClick() {
    mCurrEdit = mTxtBill;
    mCurrEdit.requestFocus();
    new IntentIntegrator(this).initiateScan();
  }

  @OnClick(R.id.btn_camera_scan_barcode) void onBarCodeCameraClick() {
    mCurrEdit = mTxtBarcode;
    mCurrEdit.requestFocus();
    new IntentIntegrator(this).initiateScan();
  }

  @OnFocusChange({ R.id.txt_barcode, R.id.txt_bill_no }) void onEditFocusChange(View view,
      boolean hasFocus) {
    if (hasFocus) {
      mCurrEdit = (EditText) view;
    }
  }

  @OnEditorAction({ R.id.txt_barcode, R.id.txt_bill_no }) boolean onEditAction(TextView view,
      int actionId, KeyEvent keyEvent) {
    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
      switch (view.getId()) {
        case R.id.txt_bill_no: {
          queryBill();
          break;
        }
        case R.id.txt_barcode: {
          queryBarCode();
          break;
        }
      }
    }
    InputMethodManager inputMethodManager =
        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    return true;
  }

  private void queryBarCode() {
    String billNo = mTxtBill.getText().toString();
    String barCode = mTxtBarcode.getText().toString();

    mSubscriptions.add(dbCommand.rxExecute("{call sp_getDetail(?,?,?,?,?)}", settings.getLine(),
        settings.getReader(), billNo, barCode)
        .subscribeOn(Schedulers.io())
        .doOnSubscribe(() -> {
          mLoading.setVisibility(View.VISIBLE);
          mTxtResponse.setVisibility(View.GONE);
        })
        .doOnUnsubscribe(() -> {
          mLoading.setVisibility(View.GONE);
          mTxtResponse.setVisibility(View.VISIBLE);
        })
        .unsubscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::showDetail, this::showWarning));
  }

  private void showDetail(String s) {
    mTxtResponse.setText(s);
  }

  private void queryBill() {
    String billNo = mTxtBill.getText().toString();
    mSubscriptions.add(
        dbCommand.rxExecute("{call sp_getBill(?,?,?,?)}", settings.getLine(), settings.getReader(),
            billNo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter(str -> !TextUtils.isEmpty(str))
            .subscribe(this::showWarning, this::showWarning));
  }

  private void showWarning(Throwable throwable) {
    Timber.e(throwable, throwable.getMessage());
    showWarning(throwable.getMessage());
  }

  private void showWarning(String message) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
  }
}
