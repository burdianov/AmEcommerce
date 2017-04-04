package com.crackncrunch.amplain.resources;

import android.view.MenuItem;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.data.network.req.UserLoginReq;
import com.crackncrunch.amplain.data.network.req.UserSignInReq;
import com.crackncrunch.amplain.data.network.res.UserRes;
import com.crackncrunch.amplain.data.storage.dto.UserInfoDto;
import com.crackncrunch.amplain.mvp.presenters.MenuItemHolder;

import static org.mockito.Mockito.mock;

public class StubEntityFactory {

    @SuppressWarnings("unchecked")
    public static <T> T makeStub(Class<T> stubEntityClass) {
        switch (stubEntityClass.getSimpleName()) {
            case "UserRes":
                return (T) new UserRes("58711631a242690011b1b26d", "Lilian " +
                        "Burdianov","https://pp.userapi" +
                        ".com/c313129/v313129097/80ff/5U-iWkuFxEM.jpg",
                        "wegfvw;edcnw'lkedm93847983yuhefoij32lkml'kjvj30fewoidvn","891797111113", null);
            case "UserLoginReq":
                return (T) new UserLoginReq("anymail@mail.ru", "password");
            case "UserInfoDto":
                return (T) new UserInfoDto("Lilian Burdianov","891797111113",
                        "https://pp.userapi.com/c313129/v313129097/80ff/5U-iWkuFxEM.jpg");
            case "MenuItemHolder":
                return (T) new MenuItemHolder("Редактировать", R.drawable.ic_account_circle_black_24dp, mock(MenuItem.OnMenuItemClickListener.class));
            case "UserSignInReq":
                return (T) new UserSignInReq("Lilian", "Burdianov",
                        "https://pp.userapi.com/c313129/v313129097/80ff/5U-iWkuFxEM.jpg",
                        "lilian.burdianov@gmail.com", "891797111113");
            default:
                return null;
        }
    }
}
