package com.crackncrunch.amplain.resources;

public class TestResponses {
    public static final String SUCSESS_USER_RES_WITH_ADDRESS = "{\n" +
            "  \"_id\": \"58711631a242690011b1b26d\",\n" +
            "  \"fullName\": \"Lilian Burdianov\",\n" +
            "  \"avatarUrl\": \"https://pp.userapi.com/c313129/v313129097/80ff/5U-iWkuFxEM.jpg\",\n" +
            "  \"token\": \"wegfvw;edcnw'lkedm93847983yuhefoij32lkml'kjvj30fewoidvn\",\n" +
            "  \"phone\": \"891797111113\",\n" +
            "  \"addresses\": [\n" +
            "    {\n" +
            "      \"_id\": \"58711631a242690011b1b26d\",\n" +
            "      \"name\": \"Работа\",\n" +
            "      \"street\": \"Автостроителей\",\n" +
            "      \"house\": \"24\",\n" +
            "      \"apartment\": \"1\",\n" +
            "      \"floor\": 1,\n" +
            "      \"comment\": \"lorem\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public static final String SUCSESS_USER_RES_WITHOUT_ADDRESS = "{\n" +
            "  \"_id\": \"58711631a242690011b1b26d\",\n" +
            "  \"fullName\": \"Lilian Burdianov\",\n" +
            "  \"avatarUrl\": \"https://pp.userapi.com/c313129/v313129097/80ff/5U-iWkuFxEM.jpg\",\n" +
            "  \"token\": \"wegfvw;edcnw'lkedm93847983yuhefoij32lkml'kjvj30fewoidvn\",\n" +
            "  \"phone\": \"891797111113\",\n" +
            "  \"addresses\": []\n" +
            "}";

    public static final String SUCCSESS_GET_PRODUCTS = "[\n" +
            "              {\n" +
            "                \"_id\": \"584f93c0ed0d00001179ac89\",\n" +
            "                \"productName\": \"test 1\",\n" +
            "                \"imageUrl\": \"http://skill-branch.ru/img/app/product-01.jpg\",\n" +
            "                \"description\": \"description 1 description 1 description 1 description 1 description 1\",\n" +
            "                \"price\": 100,\n" +
            "                \"raiting\": 4.2,\n" +
            "                \"active\": false,\n" +
            "                \"comments\": [\n" +
            "                  {\n" +
            "                    \"_id\": \"58712357a242690011b1b278\",\n" +
            "                    \"avatar\": \"http://skill-branch.ru/img/app/avatar-1.png\",\n" +
            "                    \"raiting\": 3,\n" +
            "                    \"commentDate\": \"2016-11-15T04:58:08.000Z\",\n" +
            "                    \"comment\": \"Lorem ipsum dolor sit amet. Quas molestias excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Beatae vitae dicta sunt, explicabo ut enim. Excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Id, quod maxime placeat, facere possimus. \",\n" +
            "                    \"active\": true\n" +
            "                  },\n" +
            "                  {\n" +
            "                    \"_id\": \"58712357a242690011b1b279\",\n" +
            "                    \"avatar\": \"http://skill-branch.ru/img/app/avatar-1.png\",\n" +
            "                    \"raiting\": 3,\n" +
            "                    \"commentDate\": \"2016-11-15T04:58:08.000Z\",\n" +
            "                    \"comment\": \"Lorem ipsum dolor sit amet. Quas molestias excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Beatae vitae dicta sunt, explicabo ut enim. Excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Id, quod maxime placeat, facere possimus. \",\n" +
            "                    \"active\": true\n" +
            "                  }\n" +
            "                ]\n" +
            "              },\n" +
            "              {\n" +
            "                \"_id\": \"584f93cfed0d00001179ac8a\",\n" +
            "                \"productName\": \"test 2\",\n" +
            "                \"imageUrl\": \"http://skill-branch.ru/img/app/product-02.jpg\",\n" +
            "                \"description\": \"description 2 description 2 description 2 description 2 description 2\",\n" +
            "                \"price\": 200,\n" +
            "                \"raiting\": 4.2,\n" +
            "                \"active\": true,\n" +
            "                \"comments\": [\n" +
            "                  {\n" +
            "                    \"_id\": \"58712357a242690011b1b278\",\n" +
            "                    \"avatar\": \"http://skill-branch.ru/img/app/avatar-1.png\",\n" +
            "                    \"raiting\": 3,\n" +
            "                    \"commentDate\": \"2016-11-15T04:58:08.000Z\",\n" +
            "                    \"comment\": \"Lorem ipsum dolor sit amet. Quas molestias excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Beatae vitae dicta sunt, explicabo ut enim. Excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Id, quod maxime placeat, facere possimus. \",\n" +
            "                    \"active\": true\n" +
            "                  },\n" +
            "                  {\n" +
            "                    \"_id\": \"58712357a242690011b1b278\",\n" +
            "                    \"avatar\": \"http://skill-branch.ru/img/app/avatar-1.png\",\n" +
            "                    \"raiting\": 3,\n" +
            "                    \"commentDate\": \"2016-11-15T04:58:08.000Z\",\n" +
            "                    \"comment\": \"Lorem ipsum dolor sit amet. Quas molestias excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Beatae vitae dicta sunt, explicabo ut enim. Excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Id, quod maxime placeat, facere possimus. \",\n" +
            "                    \"active\": true\n" +
            "                  },\n" +
            "                  {\n" +
            "                  \n" +
            "                    \"_id\": \"58712357a242690011b1b278\",\n" +
            "                    \"avatar\": \"http://skill-branch.ru/img/app/avatar-1.png\",\n" +
            "                    \"raiting\": 3,\n" +
            "                    \"commentDate\": \"2016-11-15T04:58:08.000Z\",\n" +
            "                    \"comment\": \"Lorem ipsum dolor sit amet. Quas molestias excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Beatae vitae dicta sunt, explicabo ut enim. Excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Id, quod maxime placeat, facere possimus. \",\n" +
            "                    \"active\": true\n" +
            "                  }\n" +
            "                ]\n" +
            "              },\n" +
            "              {\n" +
            "                \"_id\": \"584f93e2ed0d00001179ac8b\",\n" +
            "                \"productName\": \"test 3\",\n" +
            "                \"imageUrl\": \"http://skill-branch.ru/img/app/product-03.jpg\",\n" +
            "                \"description\": \"description 3 description 3 description 3 description 3 description 3\",\n" +
            "                \"price\": 300,\n" +
            "                \"raiting\": 4.2,\n" +
            "                \"active\": true,\n" +
            "                \"comments\": [\n" +
            "                  {\n" +
            "                    \"_id\": \"58712357a242690011b1b278\",\n" +
            "                    \"avatar\": \"http://skill-branch.ru/img/app/avatar-1.png\",\n" +
            "                    \"raiting\": 3,\n" +
            "                    \"commentDate\": \"2016-11-15T04:58:08.000Z\",\n" +
            "                    \"comment\": \"Lorem ipsum dolor sit amet. Quas molestias excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Beatae vitae dicta sunt, explicabo ut enim. Excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Id, quod maxime placeat, facere possimus. \",\n" +
            "                    \"active\": true\n" +
            "                  }\n" +
            "                ]\n" +
            "              },\n" +
            "              {\n" +
            "                \"_id\": \"584f9409ed0d00001179ac8c\",\n" +
            "                \"productName\": \"test 4\",\n" +
            "                \"imageUrl\": \"http://skill-branch.ru/img/app/product-04.jpg\",\n" +
            "                \"description\": \"description 4 description 4 description 4 description 4 description 4\",\n" +
            "                \"price\": 400,\n" +
            "                \"raiting\": 4.2,\n" +
            "                \"active\": true,\n" +
            "                \"comments\": [\n" +
            "                  {\n" +
            "                    \"_id\": \"58712357a242690011b1b278\",\n" +
            "                    \"avatar\": \"http://skill-branch.ru/img/app/avatar-1.png\",\n" +
            "                    \"raiting\": 3,\n" +
            "                    \"commentDate\": \"2016-11-15T04:58:08.000Z\",\n" +
            "                    \"comment\": \"Lorem ipsum dolor sit amet. Quas molestias excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Beatae vitae dicta sunt, explicabo ut enim. Excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Id, quod maxime placeat, facere possimus. \",\n" +
            "                    \"active\": true\n" +
            "                  },\n" +
            "                  {\n" +
            "                    \"_id\": \"58712357a242690011b1b278\",\n" +
            "                    \"avatar\": \"http://skill-branch.ru/img/app/avatar-1.png\",\n" +
            "                    \"raiting\": 3,\n" +
            "                    \"commentDate\": \"2016-11-15T04:58:08.000Z\",\n" +
            "                    \"comment\": \"Lorem ipsum dolor sit amet. Quas molestias excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Beatae vitae dicta sunt, explicabo ut enim. Excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Id, quod maxime placeat, facere possimus. \",\n" +
            "                    \"active\": true\n" +
            "                  },\n" +
            "                  {\n" +
            "                    \"_id\": \"58712357a242690011b1b278\",\n" +
            "                    \"avatar\": \"http://skill-branch.ru/img/app/avatar-1.png\",\n" +
            "                    \"raiting\": 3,\n" +
            "                    \"commentDate\": \"2016-11-15T04:58:08.000Z\",\n" +
            "                    \"comment\": \"Lorem ipsum dolor sit amet. Quas molestias excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Beatae vitae dicta sunt, explicabo ut enim. Excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Id, quod maxime placeat, facere possimus. \",\n" +
            "                    \"active\": false\n" +
            "                  },\n" +
            "                  {\n" +
            "                    \"_id\": \"58712357a242690011b1b278\",\n" +
            "                    \"avatar\": \"http://skill-branch.ru/img/app/avatar-1.png\",\n" +
            "                    \"raiting\": 3,\n" +
            "                    \"commentDate\": \"2016-11-15T04:58:08.000Z\",\n" +
            "                    \"comment\": \"Lorem ipsum dolor sit amet. Quas molestias excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Beatae vitae dicta sunt, explicabo ut enim. Excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Id, quod maxime placeat, facere possimus. \",\n" +
            "                    \"active\": true\n" +
            "                  }\n" +
            "                ]\n" +
            "              },\n" +
            "              {\n" +
            "                \"_id\": \"584f9429ed0d00001179ac8d\",\n" +
            "                \"productName\": \"test 5\",\n" +
            "                \"imageUrl\": \"http://skill-branch.ru/img/app/product-05.jpg\",\n" +
            "                \"description\": \"description 5 description 5 description 5 description 5 description 5\",\n" +
            "                \"price\": 100,\n" +
            "                \"raiting\": 4.2,\n" +
            "                \"active\": true,\n" +
            "                \"comments\": [\n" +
            "                  {\n" +
            "                    \"_id\": \"58712357a242690011b1b278\",\n" +
            "                    \"avatar\": \"http://skill-branch.ru/img/app/avatar-1.png\",\n" +
            "                    \"raiting\": 3,\n" +
            "                    \"commentDate\": \"2016-11-15T04:58:08.000Z\",\n" +
            "                    \"comment\": \"Lorem ipsum dolor sit amet. Quas molestias excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Beatae vitae dicta sunt, explicabo ut enim. Excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Id, quod maxime placeat, facere possimus. \",\n" +
            "                    \"active\": true\n" +
            "                  },\n" +
            "                  {\n" +
            "                    \"_id\": \"58712357a242690011b1b278\",\n" +
            "                    \"avatar\": \"http://skill-branch.ru/img/app/avatar-1.png\",\n" +
            "                    \"raiting\": 3,\n" +
            "                    \"commentDate\": \"2016-11-15T04:58:08.000Z\",\n" +
            "                    \"comment\": \"Lorem ipsum dolor sit amet. Quas molestias excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Beatae vitae dicta sunt, explicabo ut enim. Excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Id, quod maxime placeat, facere possimus. \",\n" +
            "                    \"active\": false\n" +
            "                  }\n" +
            "                ]\n" +
            "              },\n" +
            "              {\n" +
            "                \"_id\": \"584f9437ed0d00001179ac8e\",\n" +
            "                \"productName\": \"test 6\",\n" +
            "                \"imageUrl\": \"http://skill-branch.ru/img/app/product-06.jpg\",\n" +
            "                \"description\": \"description 6 description 6 description 6 description 6 description 6\",\n" +
            "                \"price\": 600,\n" +
            "                \"raiting\": 4.2,\n" +
            "                \"active\": true,\n" +
            "                \"comments\": [\n" +
            "                  {\n" +
            "                    \"_id\": \"58712357a242690011b1b278\",\n" +
            "                    \"avatar\": \"http://skill-branch.ru/img/app/avatar-1.png\",\n" +
            "                    \"raiting\": 3,\n" +
            "                    \"commentDate\": \"2016-11-15T04:58:08.000Z\",\n" +
            "                    \"comment\": \"Lorem ipsum dolor sit amet. Quas molestias excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Beatae vitae dicta sunt, explicabo ut enim. Excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Id, quod maxime placeat, facere possimus. \",\n" +
            "                    \"active\": true\n" +
            "                  },\n" +
            "                  {\n" +
            "                    \"_id\": \"58712357a242690011b1b278\",\n" +
            "                    \"avatar\": \"http://skill-branch.ru/img/app/avatar-1.png\",\n" +
            "                    \"raiting\": 3,\n" +
            "                    \"commentDate\": \"2016-11-15T04:58:08.000Z\",\n" +
            "                    \"comment\": \"Lorem ipsum dolor sit amet. Quas molestias excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Beatae vitae dicta sunt, explicabo ut enim. Excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Id, quod maxime placeat, facere possimus. \",\n" +
            "                    \"active\": false\n" +
            "                  },\n" +
            "                  {\n" +
            "                    \"_id\": \"58712357a242690011b1b278\",\n" +
            "                    \"avatar\": \"http://skill-branch.ru/img/app/avatar-1.png\",\n" +
            "                    \"raiting\": 3,\n" +
            "                    \"commentDate\": \"2016-11-15T04:58:08.000Z\",\n" +
            "                    \"comment\": \"Lorem ipsum dolor sit amet. Quas molestias excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Beatae vitae dicta sunt, explicabo ut enim. Excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Id, quod maxime placeat, facere possimus. \",\n" +
            "                    \"active\": true\n" +
            "                  },\n" +
            "                  {\n" +
            "                    \"_id\": \"58712357a242690011b1b278\",\n" +
            "                    \"avatar\": \"http://skill-branch.ru/img/app/avatar-1.png\",\n" +
            "                    \"raiting\": 3,\n" +
            "                    \"commentDate\": \"2016-11-15T04:58:08.000Z\",\n" +
            "                    \"comment\": \"Lorem ipsum dolor sit amet. Quas molestias excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Beatae vitae dicta sunt, explicabo ut enim. Excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Id, quod maxime placeat, facere possimus. \",\n" +
            "                    \"active\": true\n" +
            "                  }\n" +
            "                ]\n" +
            "              },\n" +
            "              {\n" +
            "                \"_id\": \"584f9445ed0d00001179ac8f\",\n" +
            "                \"productName\": \"test 7\",\n" +
            "                \"imageUrl\": \"http://skill-branch.ru/img/app/product-07.jpg\",\n" +
            "                \"description\": \"description 7 description 7 description 7 description 7 description 7\",\n" +
            "                \"price\": 600,\n" +
            "                \"raiting\": 4.2,\n" +
            "                \"active\": true,\n" +
            "                \"comments\": []\n" +
            "              },\n" +
            "              {\n" +
            "                \"_id\": \"58712357a242690011b1b278\",\n" +
            "                \"productName\": \"test 8\",\n" +
            "                \"imageUrl\": \"http://skill-branch.ru/img/app/product-08.jpg\",\n" +
            "                \"description\": \"description 8 description 8 description 8 description 8 description 8\",\n" +
            "                \"price\": 600,\n" +
            "                \"raiting\": 4.2,\n" +
            "                \"active\": true,\n" +
            "                \"comments\": [\n" +
            "                  {\n" +
            "                    \"_id\": \"58712357a242690011b1b278\",\n" +
            "                    \"avatar\": \"http://skill-branch.ru/img/app/avatar-1.png\",\n" +
            "                    \"raiting\": 3,\n" +
            "                    \"commentDate\": \"2016-11-15T04:58:08.000Z\",\n" +
            "                    \"comment\": \"Lorem ipsum dolor sit amet. Quas molestias excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Beatae vitae dicta sunt, explicabo ut enim. Excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Id, quod maxime placeat, facere possimus. \",\n" +
            "                    \"active\": true\n" +
            "                  }\n" +
            "                ]\n" +
            "              },\n" +
            "              {\n" +
            "                \"_id\": \"584f944fed0d00001179ac90\",\n" +
            "                \"productName\": \"test 9\",\n" +
            "                \"imageUrl\": \"http://skill-branch.ru/img/app/product-08.jpg\",\n" +
            "                \"description\": \"description 8 description 8 description 8 description 8 description 8\",\n" +
            "                \"price\": 900,\n" +
            "                \"raiting\": 4.2,\n" +
            "                \"active\": false,\n" +
            "                \"comments\": [\n" +
            "                  {\n" +
            "                    \"_id\": \"58712357a242690011b1b278\",\n" +
            "                    \"avatar\": \"http://skill-branch.ru/img/app/avatar-1.png\",\n" +
            "                    \"raiting\": 3,\n" +
            "                    \"commentDate\": \"2016-11-15T04:58:08.000Z\",\n" +
            "                    \"comment\": \"Lorem ipsum dolor sit amet. Quas molestias excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Beatae vitae dicta sunt, explicabo ut enim. Excepturi sint, obcaecati cupiditate non numquam eius modi tempora incidunt. Id, quod maxime placeat, facere possimus. \",\n" +
            "                    \"active\": false\n" +
            "                  }\n" +
            "                ]\n" +
            "              }\n" +
            "            ]";

    public static final String VK_PROFILE_RES_SUCCESS = "{\n" +
            "  \"response\": [\n" +
            "    {\n" +
            "      \"uid\": 30405097,\n" +
            "      \"first_name\": \"Lilian\",\n" +
            "      \"last_name\": \"Burdianov\",\n" +
            "      \"photo_200\": \"https://pp.userapi.com/c313129/v313129097/80ff/5U-iWkuFxEM.jpg\",\n" +
            "      \"mobile_phone\": \"89179711113\",\n" +
            "      \"home_phone\": \"89179716463\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public static final String FB_PROFILE_RES_SUCCESS = "{\n" +
            "  \"email\": \"lilian.burdianov@gmail.com\",\n" +
            "  \"picture\": {\n" +
            "    \"data\": {\n" +
            "      \"height\": 200,\n" +
            "      \"is_silhouette\": false,\n" +
            "      \"url\": \"https://scontent.xx.fbcdn.net/v/t1.0-1/p200x200/12342782_1515567822069044_6374123212258412889_n.jpg?oh=2548c1824eaa4cccd071aa920b96d7af&oe=59627FC4\",\n" +
            "      \"width\": 200\n" +
            "    }\n" +
            "  },\n" +
            "  \"first_name\": \"Lilian\",\n" +
            "  \"last_name\": \"Burdianov\",\n" +
            "  \"id\": \"1682199162072575\"\n" +
            "}" ;

}
