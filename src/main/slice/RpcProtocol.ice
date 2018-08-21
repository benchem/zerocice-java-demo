#pragma once
#include <Ice/Identity.ice>

module team
{
    module benchem
    {
        module communication
        {
            interface JsonServicePortal
            {
                string invoke(string requestBody);
            }

            interface JsonServiceCenter
            {
                void register(string clientTag, Ice::Identity ident);

                void unRegister(string clientTag, Ice::Identity ident);

                void cast(string requestBody);

                string forward(string clientTag, string requestBody);
            }
        }
    }
}