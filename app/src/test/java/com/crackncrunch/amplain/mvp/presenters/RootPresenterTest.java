package com.crackncrunch.amplain.mvp.presenters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.ViewPager;

import com.crackncrunch.amplain.data.storage.dto.ActivityResultDto;
import com.crackncrunch.amplain.data.storage.dto.UserInfoDto;
import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.di.components.AppComponent;
import com.crackncrunch.amplain.di.components.DaggerAppComponent;
import com.crackncrunch.amplain.di.modules.AppModule;
import com.crackncrunch.amplain.di.modules.RootModule;
import com.crackncrunch.amplain.mvp.models.AccountModel;
import com.crackncrunch.amplain.mvp.views.IRootView;
import com.crackncrunch.amplain.resources.StubEntityFactory;
import com.crackncrunch.amplain.ui.activities.DaggerRootActivity_RootComponent;
import com.crackncrunch.amplain.ui.activities.RootActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

/**
 * Created by Lilian on 25-Mar-17.
 */
public class RootPresenterTest {

    @Mock
    Context mMockContext;
    @Mock
    private AccountModel mMockModel;
    @Mock
    RootActivity mMockRootView;
    private RootPresenter mPresenter;
    private BundleServiceRunner mBundleServiceRunner;
    private MortarScope mMortarScope;
    private RootActivity.RootComponent mTestRootComponent;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        prepareDependency(); //подготавливаем Dependency
        prepareScope(); //подготавливаем Scope
        prepareRxSchedulers(); //подготавливаем Shedulers
        prepareStubs(); // подготавливаем заглушки

        mPresenter = new RootPresenter();
    }

    private void prepareDependency() {
        AppComponent testAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(mMockContext))
                .build();


        mTestRootComponent = DaggerRootActivity_RootComponent.builder()
                .appComponent(testAppComponent)
                .rootModule(new RootModule() {
                    @Override
                    public AccountModel provideAccountModel() {
                        return mMockModel; //мок модель для RootPresenter можно переопределить инъекции даггера исспользуя новые testComponent и TestModule наследуемые это production Component/Modules
                    }

                    @Override
                    public RootPresenter provideRootPresenter() {
                        return mock(RootPresenter.class); //переопределяем презентер для вью не несет ключевого значения поэтому мока
                    }
                })
                .build();
    }

    private void prepareScope() {
        mBundleServiceRunner = new BundleServiceRunner();
        mMortarScope = MortarScope.buildRootScope()
                .withService(BundleServiceRunner.SERVICE_NAME, mBundleServiceRunner)
                .withService(DaggerService.SERVICE_NAME, mTestRootComponent)
                .build("MockRoot");
    }

    private void prepareRxSchedulers() {
        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate(); //без этого AndroidScheduler.mainThread -> NPE
            }
        });
    }

    private void prepareStubs() {
        //noinspection WrongConstant
        given(mMockRootView.getSystemService(BundleServiceRunner.SERVICE_NAME)).willReturn(mBundleServiceRunner);
        //noinspection WrongConstant
        given(mMockRootView.getSystemService(MortarScope.class.getName())).willReturn(mMortarScope);
        given(mMockModel.getUserInfoSbj()).willReturn(BehaviorSubject.create());
    }

    @After
    public void tearDown() throws Exception {
        mPresenter.dropView(mMockRootView);
    }

    @Test
    public void onLoad_takeView_INIT_DRAWER() throws Exception {
        //given
        UserInfoDto stubUserInfo = StubEntityFactory.makeStub(UserInfoDto.class);
        given(mMockModel.getUserInfoSbj()).willReturn(BehaviorSubject.create(stubUserInfo));

        //when
        mPresenter.takeView(mMockRootView);

        //then
        then(mMockModel).should(times(1)).getUserInfoSbj();
        then(mMockRootView).should(never()).showError(any());
        then(mMockRootView).should().initDrawer(stubUserInfo);
    }

    @Test
    public void getRootView_takeView_INSTANCE_OF_ROOT_ACTIVITY() throws Exception {
        //given
        mPresenter.takeView(mMockRootView); // условие что вью привязана к презентеру

        //when
        IRootView actualView = mPresenter.getRootView(); //возвращаемая вью

        //then
        assertTrue(actualView instanceof RootActivity); //возвращаемая вью инстанс RootActivity
    }

    @Test
    public void newActionBarBuilder_call_NEW_BUILDER_OBJECT() throws Exception {
        //given

        //when
        RootPresenter.ActionBarBuilder actualBuilder1 = mPresenter.newActionBarBuilder(); //получаем экземпляр 1
        RootPresenter.ActionBarBuilder actualBuilder2 = mPresenter.newActionBarBuilder(); //получаем экземпляр 2

        //then
        assertNotEquals(actualBuilder1, actualBuilder2); //проверяем что newActionBarBuilder возвращает разные экземпляры

    }

    @Test
    public void ActionBarBuilder_buildWithTabs_CALL_ACTIVITY_SET_TABLAYOUT() throws Exception {
        //given
        // тут stub ожидаемых результатов
        String expectedTitle = "title";
        boolean expectedArrow = true;
        MenuItemHolder expectedAction1 = StubEntityFactory.makeStub(MenuItemHolder.class);
        MenuItemHolder expectedAction2 = StubEntityFactory.makeStub(MenuItemHolder.class);
        ArrayList<MenuItemHolder> expectedActions = new ArrayList<>();
        expectedActions.add(expectedAction1);
        expectedActions.add(expectedAction2);
        ViewPager mockTab = mock(ViewPager.class);

        mPresenter.takeView(mMockRootView); //  условие что вью привязана к презентеру

        //when
        RootPresenter.ActionBarBuilder actualBuilder = mPresenter.newActionBarBuilder(); // получаем экземпляр билдера
        actualBuilder.setTitle(expectedTitle)   //устанавливаем заголовок
                .setBackArrow(expectedArrow)    //устанавливаем стрелку назад
                .addAction(expectedAction1)     //добавляем экшен
                .addAction(expectedAction2)     //добавляем экшен
                .setTab(mockTab)                //добавляем табы
                .build();                       // билд!!!

        //then
        then(mMockRootView).should(times(1)).setTitle(expectedTitle);        //в активити вызван метод установки заголовка 1 раз
        then(mMockRootView).should(times(1)).setBackArrow(expectedArrow );   //в активити вызван метод установки стрелки 1 раз
        then(mMockRootView).should(times(1)).setMenuItem(expectedActions);   //в активити вызваны методы установки экшенов 1 раз
        then(mMockRootView).should(times(1)).setTabLayout(mockTab);          //в активити установились табы 1 раз
    }

    @Test
    public void ActionBarBuilder_buildWithoutTabs_CALL_ACTIVITY_REMOVE_TABLAYOUT() throws Exception {
        //given
        String expectedTitle = "title 2";
        boolean expectedArrow = false;
        MenuItemHolder expectedAction1 = StubEntityFactory.makeStub(MenuItemHolder.class);
        ArrayList<MenuItemHolder> expectedActions = new ArrayList<>();
        expectedActions.add(expectedAction1);

        mPresenter.takeView(mMockRootView);

        //when
        RootPresenter.ActionBarBuilder actualBuilder = mPresenter.newActionBarBuilder();
        actualBuilder.setTitle(expectedTitle)
                .setBackArrow(expectedArrow)
                .addAction(expectedAction1)
                .build();

        //then
        then(mMockRootView).should(times(1)).setTitle(expectedTitle);
        then(mMockRootView).should(times(1)).setBackArrow(expectedArrow );
        then(mMockRootView).should(times(1)).setMenuItem(expectedActions);
        then(mMockRootView).should(times(1)).removeTabLayout();              //табы были удалены
    }

    @Test
    public void onActivityResult_stubActivityResult_NOT_COMPLETE_OBS() throws Exception {
        //given
        int expectedRequestCode = 1;
        int expectedResultCode = Activity.RESULT_OK;
        Intent expectedIntent = mock(Intent.class);
        TestSubscriber<ActivityResultDto> subscriber = new TestSubscriber<>();

        mPresenter.takeView(mMockRootView);      //  условие что вью привязана к презентеру

        //when
        PublishSubject<ActivityResultDto> subj = mPresenter.getActivityResultSubject();    //получаем последовательность
        subj.subscribe(subscriber);                                                        //подписываемся на нее
        mPresenter.onActivityResultHandler(expectedRequestCode, expectedResultCode, expectedIntent); //имитируем получение результата из активити

        //then
        subscriber.assertNotCompleted(); //подписка не заканчивается (горячая последовательность)
        subscriber.assertNoErrors();    //без ошибок

        ActivityResultDto actualActivityResult = subscriber.getOnNextEvents().get(0);   //получаем результат (первый эмит последовательности)
        assertEquals(expectedRequestCode, actualActivityResult.getRequestCode());       // проверяем значения на соответствия
        assertEquals(expectedResultCode, actualActivityResult.getResultCode());         // проверяем значения на соответствия
        assertEquals(expectedIntent, actualActivityResult.getIntent());                 // проверяем значения на соответствия
    }

    @Test
    public void checkPermissionsAndRequestIfNotGranted_permissionsGranted_NOT_REQUEST_PERMISSION() throws Exception {
        //given
        int expectedRequestCode = 1;
        //требуются granted permissons
        String[] expectedPermission = new String[]{
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.READ_EXTERNAL_STORAGE"
        };
        mPresenter.takeView(mMockRootView);                                              //  условие что вью привязана к презентеру
        given(mMockRootView.isAllGranted(expectedPermission, false)).willReturn(true);   //  все разрешения разрешены

        //when
        mPresenter.checkPermissionsAndRequestIfNotGranted(expectedPermission, expectedRequestCode); //проверяем что есть из разрешений

        //then
        then(mMockRootView).should(never()).requestPermissions(expectedPermission, expectedRequestCode); // запрос разрешенй никогда не вызывается
    }

    @Test
    public void checkPermissionsAndRequestIfNotGranted_permissionsDenied_Sdk23_REQUEST_PERMISSION() throws Exception {
        //given
        int expectedRequestCode = 1;
        String[] expectedPermission = new String[]{
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.READ_EXTERNAL_STORAGE"
        };
        mPresenter.takeView(mMockRootView);
        given(mMockRootView.isAllGranted(expectedPermission, false)).willReturn(false);  //  все разрешения отклонены
        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 23);                    //версия SDK 23

        //when
        mPresenter.checkPermissionsAndRequestIfNotGranted(expectedPermission, expectedRequestCode);     //проверить разрешения

        //then
        then(mMockRootView).should(times(1)).requestPermissions(expectedPermission, expectedRequestCode); // запрос разрешенй вызывается 1 раз
    }

    @Test
    public void checkPermissionsAndRequestIfNotGranted_permissionsDenied_Sdk19_NOT_REQUEST_PERMISSION() throws Exception {
        //given
        int expectedRequestCode = 1;
        String[] expectedPermission = new String[]{
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.READ_EXTERNAL_STORAGE"
        };
        mPresenter.takeView(mMockRootView);
        given(mMockRootView.isAllGranted(expectedPermission, false)).willReturn(false);  //  все разрешения отклонены
        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 19);                    //версия SDK 19

        //when
        mPresenter.checkPermissionsAndRequestIfNotGranted(expectedPermission, expectedRequestCode);     //проверить разрешения

        //then
        then(mMockRootView).should(never()).requestPermissions(expectedPermission, expectedRequestCode); // запрос разрешенй никогда не вызывается
    }

    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiedField = Field.class.getDeclaredField("modifiers");
        modifiedField.setAccessible(true);
        modifiedField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);

    }

    @Test
    public void subscribeOnProductCountSbj_updateProductCount_UPDATE_CART_COUNTER() throws Exception {
        //given
        int expectedCount = 1;
        BehaviorSubject<Integer> expectedSbj = BehaviorSubject.create();
        given(mMockModel.getProductCountSbj()).willReturn(expectedSbj);
        mPresenter.takeView(mMockRootView);

        //when
        expectedSbj.onNext(expectedCount);
        expectedSbj.onNext(expectedCount);
        expectedSbj.onNext(expectedCount);
        expectedSbj.onNext(expectedCount);

        //then
        then(mMockRootView).should(times(4)).updateCartCounter(expectedCount);
        then(mMockRootView).should(never()).showError(any());
    }
}