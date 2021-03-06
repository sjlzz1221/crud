/*
 *
 * Copyright 2020-2020 HuiFer All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.huifer.mybatis.plus.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.huifer.crud.common.annotation.CacheKey;
import com.github.huifer.crud.mybatis.plus.interfaces.AforMybatisPlus;
import com.github.huifer.mybatis.plus.mybatis.entity.IssuesEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@CacheKey(key = "issues", type = IssuesEntity.class)
public interface IssuesMapper extends BaseMapper<IssuesEntity>,
    AforMybatisPlus<Integer, IssuesEntity> {

}
