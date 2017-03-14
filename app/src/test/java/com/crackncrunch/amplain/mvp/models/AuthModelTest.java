package com.crackncrunch.amplain.mvp.models;

import com.birbit.android.jobqueue.JobManager;
import com.crackncrunch.amplain.data.managers.DataManager;
import com.crackncrunch.amplain.data.network.req.UserLoginReq;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

/**
 * Created by Lilian on 11-Mar-17.
 */
public class AuthModelTest {

    private AuthModel model;

    @Mock
    DataManager mockDataManager;
    @Mock
    JobManager mockJobManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        model = new AuthModel(mockDataManager, mockJobManager);
    }

    @Test
    public void isAuthUser() throws Exception {
        model.isAuthUser();
        verify(mockDataManager, only()).isAuthUser();
    }

    @Test
    public void loginUser() throws Exception {
        model.loginUser("anymail@mail.ru", "password");
        verify(mockDataManager, only()).loginUser(any(UserLoginReq.class));
    }

}