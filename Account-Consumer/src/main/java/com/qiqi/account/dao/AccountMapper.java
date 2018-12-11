package com.qiqi.account.dao;

import com.qiqi.account.model.TbAccount;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountMapper {

    TbAccount getByUId(@Param("uid") int uid);

    TbAccount getByPhone(@Param("phonenumber") String phonenumber);

    TbAccount getByMail(@Param("emailaddress") String emailaddress);

    TbAccount getByAccountName(@Param("accountname") String accountname);

    int saveOrUpdateByUId(TbAccount fxAccount);

    int saveOrUpdateByPhone(TbAccount fxAccount);

    int save(TbAccount fxAccount);
    
    void updateUserTokenByUid(@Param("uid") int uid);

    TbAccount getPhoneNumberByUId(@Param("uid") Integer uid);

}
