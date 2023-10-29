//
// Copyright (C) 2023 The LineageOS Project
//
// SPDX-License-Identifier: Apache-2.0
//

#pragma once

#include <aidl/vendor/lineage/xiaomitouch/BnXiaomiTouch.h>

#include <mutex>
#include <thread>

namespace aidl::vendor::lineage::xiaomitouch {

class XiaomiTouch : public BnXiaomiTouch {
    public:
    XiaomiTouch(void);
    ::ndk::ScopedAStatus setModeValue(int mode, int value) override;
};

}