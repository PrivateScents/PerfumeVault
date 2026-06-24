package com.perfumevault.data

/**
 * Hilfsklasse um den Katalog einfach zu erweitern.
 * Du kannst hier Marken einfach hinzufügen. 
 * Namen können entweder ein String "Name" oder ein Pair "Name" to "Url" sein.
 */
object CatalogDefaults {

    fun get() = listOf<CatalogPerfume>()
        .addBrand(
            "Anfas", "EDP", "75", listOf(
                "Salam" to "https://cdn.50-ml.media/media/catalog/product/a/n/anfas_salam_eau_de_parfum_2.jpg?optimize=medium&bg-color=255,255,255&fit=bounds&height=540&width=540",
                "Gaya" to "https://anfascollection.com/cdn/shop/files/gaya-prod2_1024x1024.jpg?v=1753957739",
                "Ishq" to "https://www.4doutfitters.com/cdn/shop/files/IMG-4679.jpg?v=1724280286&width=1800",
                "Dhai" to "https://www.marcgebauer.com/cdn/shop/files/Blue_800x.jpg?v=1774453714",
                "Watan" to "https://www.thescenthouse.com/cdn/shop/files/anfas-WatanGold3.jpg?v=1719220194&width=1000",
                "Rahaba" to "https://www.essence-garden.de/cdn/shop/files/rahaba_anfas_parfum_mood.jpg?v=1775248539",
                "Sa'ah" to "https://www.myperfumeshop.qa/cdn/shop/files/nnnnn_00896042-fe23-4261-99c6-0b725205b019.png?v=1712550053&width=1500",
                "Shaghaf" to "https://lifestyleperfume.am/images/product/3082/1670320902-3[1].jpg",
                "Dari" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTrJOOh2Jl_YtfSb0VxhUWE2D1mCB_8NvCYat2jsOwtgf1BUTitbw26LKlf&s=10",
                "Sama" to "https://fimgs.net/mdimg/secundar/o.153404.jpg"
            )
        )
        .addBrand(
            "Acqua di Parma", "EDP", "30,75,100,150", listOf(
                "Arancia di Capri" to "https://static.sweetcare.com/img/prd/488/v-638864675083287358/acqua-di-parma-023807aq-2.webp",
                "Fico di Amalfi" to "https://www.beautytheshop.com/imgs/productos_cosmetica/resized/680x680/8028713825873_3.webp",
                "Arancia di Capri La Riserva" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR16ILAUaiM4KzcF7GapE94WKSnjnExb9gQ9C8ZF5arSCABt6HZDirLJuQ&s=10",
                "Bergamotto di Calabria" to "https://beauty.at/parfum/exclusive/Acqua-di-Parma-Bergamotto-di-Calabria-La-Spugnatura/Acqua-di-Parma-3.jpg?v=1619111557&version=widecontent",
                "Mandarino di Sicilia" to "https://media.parfumo.com/perfume_imagery/ec/ece1e0-mandarino-di-sicilia-acqua-di-parma_1200.jpg",
                "Mirto di Panarea" to "https://www.beautytheshop.com/imgs/productos_cosmetica/resized/680x680/8028713822216_3.webp",
            )
        )
        .addBrand(
            "Atkinsons", "EDP", "100", listOf(
                "The British Bouquet" to "https://fandi-perfume.com/cdn/shop/files/atkinsons-the-british-bouquet-unisex-eau-de-toilette-1217974064.png?v=1769437718&width=1024",
                "41 Burlington Arcade" to "https://perfume-essence.com/media/catalog/product/cache/9110975d72d9707b7bc93e05e75366a6/a/t/atbltarc_2_1.jpeg",
                "Oud Save The King" to "https://cdn.notinoimg.com/detail_main_lq/atkinsons/8002135119079_03/oud-save-the-king___240612.jpg",
                "Oud Save The Queen" to "https://cdn.notinoimg.com/detail_main_lq/atkinsons/8011003867196_03/oud-save-the-queen___240612.jpg",
                "24 Old Bond Street" to "https://osswald.ch/cdn/shop/files/24oldbondstreetmood_800x.jpg?v=1705049528",
                "Mint & Tonic" to "https://cdn.notinoimg.com/detail_main_lq/atkinsons/8002135152274_03/mint-tonic___240612.jpg",
                "His Majesty The Oud" to "https://media.zid.store/bfaa3454-0e90-4f91-91c7-be4cb1752ac8/f2db0c53-f3f9-4c2c-ac86-074fcd6a4ce4.webp",
                "Her Majesty The Oud" to "https://cdn.notinoimg.com/detail_main_lq/atkinsons/8002135139183_03/her-majesty-the-oud___240612.jpg",
                "Robinson Bear" to "https://www.scentstore.com/wp-content/uploads/2022/01/Atkinsons-Robinson-Bear-Packshot.jpg",
                "Pirates' Grand Reserve" to "https://osswald.ch/cdn/shop/files/piratesgrandreservemood_1000x.jpg?v=1705054815"
            )
        )

        .addBrand(
            "bdk Parfums", "EDP", "100", listOf(
                "Gris Charnel (EdP)" to "https://media.zid.store/bfaa3454-0e90-4f91-91c7-be4cb1752ac8/79ef5b04-9277-4232-9b8d-839fecb24fac.jpg",
                "Gris Charnel Extrait" to "https://www.4doutfitters.com/cdn/shop/files/Gris-Charnel-extrait_hero.png?v=1756109579&width=1776",
                "Rouge Smoking" to "https://ik.imagekit.io/duftundkultur/wp-content/uploads/2025/01/bdk-extrait-de-parfum-rouge-smoking-mood-1024x1024.jpg",
                "Pas Ce Soir" to "https://cdn.shopify.com/s/files/1/0573/1785/1288/files/PascesoirExtrait-BDK.png?v=1690225044",
                "Pas Ce Soir Extrait" to "https://prod.kuoe.at/bdk-pas+ce+soir+extrait+100ml-4-768_1024_75-7621614_4.webp",
                "Velvet Tonka" to "https://us.bdkparfums.com/cdn/shop/files/VTE_00170_1600x.jpg?v=1768382586",
                "Sel d'Argent" to "https://us.bdkparfums.com/cdn/shop/files/2024_SEL_ARGENT_ROCHE_2_1600x.jpg?v=1768382345",
                "Tubereuse Imperiale" to "https://www.myperfumeshop.com.au/cdn/shop/products/bdk-parfums-tubereuse-imperiale-edp-perfume-cologne-156709.png?v=1702710365&width=1920",
                "Oud Abramad" to "https://www.scentsangel.com/cdn/shop/files/Untitleddesign_9_472a5a4c-7da1-4549-bbe4-f583115e7d73_750x.jpg?v=1751664136",
                "Villa Neroli" to "https://us.bdkparfums.com/cdn/shop/files/2024_VILLA_NEROLI_NATURE_4-5_1600x.jpg?v=1768382349"
            )
        )

        .addBrand(
            "Casamorati", "EDP", "30,100", listOf(
                "Mefisto" to "https://befrsh.com/cdn/shop/files/xerjoff-casamorati-mefisto_800x800_cee42230-3633-4520-9bc8-ae26f354455d.png?v=1779800714",
                "Mefisto Gentiluomo" to "https://media.parfumo.com/user_imagery/98/98_b0fa96de34d9937db7fa467502b2127192f11a35_1200.jpg",
                "Bouquet Ideale" to "https://www.xerjoff.com/cdn/shop/files/gallery-xerjoff-bouquetideale-still.jpg?v=1744805807&width=1946",
                "Lira" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_9690d058-13b4-4bb0-bcc5-448dac760a81.png?v=1775247872",
                "Italica" to "https://www.parfümerielaura.de/3695-large_default/casamorati-italica.jpg",
                "Dolce Amalfi" to "https://res.cloudinary.com/gentscloud/image/upload/c_fill,h_460,w_460/f_auto/q_auto/upload/product_images/10/52210.jpg",
                "Regio" to "https://theperfumemag.com/wp-content/uploads/2024/08/uvheab9fU4GiMzlc.jpg",
                "1888" to "https://cdn4.beautinow.com/wp-content/uploads/2023/08/328.2.jpg",
                "Gran Ballo" to "https://lovecareskin.de/wp-content/uploads/2026/04/Xerjoff_1888-Gran-Ballo_Fragrance-Visual.webp",
                "Fiero" to "https://media.parfumo.com/user_imagery/6a/6a_8793_1b1e3d4bdfa4f618f846ee213945f523_1200.jpg"
            )
        )

        .addBrand(
            "Christian Provenzano", "EDP", "100", listOf(
                "Ambre d'Or" to "https://puapua.de/cdn/shop/files/ambre-dor-991241_grande.jpg?v=1724975640",
                "Patchouli Noir" to "https://deparfumeur.com/6145-large_default/patchouli-noir.jpg",
                "Oud Al Fayed" to "https://www.4doutfitters.com/cdn/shop/files/OUDALFAYED2.png?v=1781032654&width=2048"
            )
        )

        .addBrand(
            "Marc Gebauer", "EDP", "100", listOf(
                "Air Tiger" to "https://i.ytimg.com/vi/lf-QvN-bJ0A/maxresdefault.jpg",
                "Orange Flamingo" to "https://www.marcgebauer.com/cdn/shop/files/Orange-Flamingo_d37c1e9e-d132-45a6-922a-dd824b89acc9_800x.jpg?v=1774937454",
                "Arabian King" to "https://www.marcgebauer.com/cdn/shop/products/RAW_1_800x.jpg?v=1774937454",
                "Arabian Cherry" to "https://www.marcgebauer.com/cdn/shop/files/2_78b5218d-9c74-47b1-9f91-859de6018c99_1000x.jpg?v=1751467130",
                "Raspberry Dictator" to "https://www.marcgebauer.com/cdn/shop/files/3_d938dcd1-a0b6-4f90-9374-0bbbfe980e2a_900x.jpg?v=1755179564",
                "Cocoon" to "https://www.marcgebauer.com/cdn/shop/files/4_d6101206-df9b-4cb8-8f35-1948a4488a09_900x.jpg?v=1751032955",
                "Purple Flamingo" to "https://www.marcgebauer.com/cdn/shop/files/Purple-Flamingo_c3ec2e29-22f6-4d0c-9814-618485cd299b_grande.jpg?v=1774471447",
                "Private Garden" to "https://us.marcgebauer.com/cdn/shop/files/2tes-Bild-Produkt_1000x_f9c77cd3-567a-4a86-b668-7c7a76170f13_800x.webp?v=1738576666",
                "Vendetta" to "https://www.marcgebauer.com/cdn/shop/files/Marc_Gebauer-Vendetta-100ml-04260766771644-image2_800x.webp?v=1774937453",
                "Killer Instinct" to "https://www.marcgebauer.com/cdn/shop/files/6_2848f6ba-2a4e-4ea8-9107-b102bbe32f56_1024x.jpg?v=1774463091",
                "Amorist" to "https://www.parfuemerie-brueckner.com/cdn/shop/files/Amorist-_jpg.webp?v=1732273200&width=320",
                "Berserker" to "https://www.marcgebauer.com/cdn/shop/files/b1_1200x.jpg?v=1678798749"
            )
        )

        .addBrand(
            "Gritti", "EDP", "100", listOf(
                "Pomelo Sorrento" to "https://galopstore.com/wp-content/uploads/2022/12/G2106_s.jpg",
                "Tutù" to "https://cdn.basler-beauty.de/out/pictures/generated/product/3/980_980_100/2667800-Gritti-White-Collection-TUTU-Extrait-de-Parfum-100-ml.fca88448.jpg",
                "Rebellion" to "https://www.marcgebauer.com/cdn/shop/files/rebellionnuovo_900x.jpg?v=1774458900",
                "Duchessa" to "https://www.4doutfitters.com/cdn/shop/products/duchessa-lifestyle.jpg?v=1710508868&width=2494",
                "Chantilly" to "https://www.ausliebezumduft.de/cdn/shop/files/ChantillyExtraitVisual.webp?v=1770793629&width=300",
                "Macaria" to "https://www.essence-garden.de/cdn/shop/files/muskaria-gritti-parfum-3197564.jpg?v=1764690554",
                "Siracusa" to "https://fimgs.net/himg/o.o7HprOmYm8e.jpg",
                "Kill The Lights" to "https://www.grittifragrances.com/cdn/shop/files/DGE00684_03.jpg?v=1758124618",
                "Adèle" to "https://product.hstatic.net/1000340570/product/adele-chuant_add8f211618043c6aeb810ba6e992844_master.jpg",
                "Decimo" to "https://content2.rozetka.com.ua/goods/images/big/316559137.jpg"
            )
        )

        .addBrand(
            "Noya", "EDP", "50", listOf(
                "Ocean's Luminescence" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_810f1fb0-7830-44d2-b698-093ed24dba1c.png?v=1777014753",
                "Saffron Dream" to "https://fimgs.net/mdimg/perfume-thumbs/375x500.87913.jpg",
                "Azure Reverie" to "https://cms.brnstc.de/product_images/1122x1536_retina/cpne/media/images/product/26/5/1003221508_8033011942764_0_1777939200000.jpg",
                "The Moon Shadow" to "https://wafadutyfree.com/cdn/shop/files/house-of-noya-eau-de-parfum-100ml-the-moon-shadow-eau-de-parfum-34778578419805_1800x1800.jpg?v=1750194815"
            )
        )

        .addBrand(
            "Initio", "EDP", "50,90", listOf(
                "Oud for Greatness" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_c6b8fca9-741c-4c74-b1a1-542e2820c031.png?v=1775248035&width=3840&quality=75",
                "Side Effect" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_79feed5b-77c8-4b43-b5a4-4582ec51a3bd.png?v=1775248038&width=3840&quality=75",
                "Rehab" to "https://www.parfuemerie-kirner.de/out/pictures/master/product/4/3(1).jpg",
                "Musk Therapy" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_212437c8-4de1-40da-8793-813c72c0d4d8.png?v=1775248032",
                "Blessed Baraka" to "https://parfumsuite.ch/cdn/shop/files/initio_blessed_baraka_lifestile.png?v=1772378009&width=1500",
                "Absolute Aphrodisiac" to "https://hrd-live.cdn.scayle.cloud/images/b31b9ace824c8874ac3dc0a0730a3c0b.jpg?brightness=1&width=922&height=1230&quality=75&bg=ffffff",
                "Atomic Rose" to "https://www.4doutfitters.com/cdn/shop/products/Initio_Atomic_Rose_Lifestyle-photo_c.jpg?v=1780213440&width=1221",
                "Oud for Happiness" to "https://www.parfuemerie-kirner.de/out/pictures/master/product/3/2(8).jpg",
                "Paragon" to "https://cdn4.beautinow.com/wp-content/uploads/2023/08/3-8.webp",
                "Narcotic Delight" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_a013a4a8-553e-4d9c-a68d-cab1910b8717.png?v=1775248033&width=3840&quality=75"
            )
        )
        .addBrand(
            "J-Scent", "EDP", "50", listOf(
                "Roasted Green Tea" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_92302988-dfe1-43db-a0e3-b6080848e920.png?v=1775248056&width=3840&quality=75",
                "Paper Soap" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_46e0c1ab-ecae-44fb-9d0b-416da519eef6.png?v=1775248056",
                "Sumo Wrestler" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_0919ea64-44b0-45d3-891f-b88da30d4668.png?v=1775248059",
                "Yuzu" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_f827e84d-b56b-4943-96d8-0f18a734245e.png?v=1775248059&width=3840&quality=75",
                "Ramune" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_0db720ed-49a7-497e-928f-9a6a49c7a2fb.png?v=1775248056&width=3840&quality=75",
                "Cherry Blossom" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_bcc558e1-5f71-4481-823c-9f7d4eae2307.png?v=1775248053",
                "Black Leather" to "https://j-scent-global.com/cdn/shop/files/edp_w16_1445x.jpg?v=1758272063",
                "Honey & Lemon" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_71ab9770-3556-4800-8a28-a2e603f07a29.png?v=1775248053&width=3840&quality=75",
                "Wood Flake" to "https://j-scent-global.com/cdn/shop/files/edp_w19.jpg?v=1758272336",
                "Hakka (Japanese Mint)" to "https://j-scent-global.com/cdn/shop/files/edp_w18.jpg?v=1758272221",
            )
        )

        .addBrand(
            "Kilian Paris", "EDP", "30,50,100", listOf(
                "Angels' Share" to "https://cdn.basler-beauty.de/out/pictures/generated/product/3/980_980_100/2510596-Kilian-Paris-Angels-Share-Eau-de-Parfum-50-ml.14c8f595.jpg",
                "Black Phantom" to "https://cdn.basler-beauty.de/out/pictures/master/product/thumb/2663376-Kilian-Paris-Fragrance-Black-Phantom-Memento-Mori-Eau-de-Parfum-100-ml.267f5906.jpg",
                "Love, Don't Be Shy" to "https://www.beautytheshop.com/imgs/productos_cosmetica/resized/680x680/3700550218227_4.webp",
                "Straight to Heaven" to "https://beautytribe.com/cdn/shop/files/by-kilian-straight-to-heaven-edp-50ml-with-coffret-151495-952853.png?v=1732790721&width=1500",
                "Good Girl Gone Bad" to "https://media.douglas.at/medias/W59qXZ1084378-4-global.jpg?context=bWFzdGVyfGltYWdlc3w0MTQ2MzF8aW1hZ2UvanBlZ3xhRGRoTDJneU9DODJNell4TXpReE9UTTRPRGsxT0M5WE5UbHhXRm94TURnME16YzRYelJmWjJ4dlltRnNMbXB3Wnd8YjdmNjk3Nzg0MzFmMzI0Y2ZiOWZhMDUwZTk4ZWM4NDc0NjlmNmNkOGU5OWUxYjk1MjM4NDU4MmFlNTcyMTI4NQ&grid=true",
                "Sacred Wood" to "https://cdn.basler-beauty.de/out/pictures/generated/product/5/1200_1200_100/1492020-Kilian-Paris-Sacred-Wood-Eau-de-Parfum-nachfuellbar-50-ml.668f12e3.jpg",
                "Bamboo Harmony" to "https://cdn.50-ml.media/media/catalog/product/k/i/kilian_bamboo_harmony_eau_de_parfum_6.jpg?optimize=medium&bg-color=255,255,255&fit=bounds&height=540&width=540",
                "Vodka on the Rocks" to "https://media.douglas.at/medias/r6qdsR531512-4-dgl-AT.jpg?context=bWFzdGVyfGltYWdlc3wzMjM5MTZ8aW1hZ2UvanBlZ3xhRE00TDJnd09DOHhNelF3TkRFd01EUTVNek0wTWk5eU5uRmtjMUkxTXpFMU1USmZORjlrWjJ3dFFWUXVhbkJufGFmMzZkYzVlZGExMzMwMDYyNjFlZjgxMmIwNWQ1Yjc3ZWJkYjU2NjRhMWEzZjBkOWQ2Nzk3OTA1ODFjMDcxMWU&grid=true",
                "Apple Brandy on the Rocks" to "https://cdn.basler-beauty.de/out/pictures/generated/product/3/1200_1200_100/2521202-Kilian-Paris-Apple-Brandy-Eau-de-Parfum-nachfuellbar-50-ml.f6286e81.jpg",
                "Can't Stop Loving You" to "https://cdn.basler-beauty.de/out/pictures/generated/product/2/1200_1200_100/1492012-Kilian-Paris-Can-t-Stop-Loving-You-Eau-de-Parfum-50-ml.67025c10.jpg",
            )
        )

        .addBrand(
            "Liquides Imaginaires", "EDP", "50,100", listOf(
                "Fortis" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_49eedff8-872e-4442-987c-f3118a8c2b89.png?v=1775248115",
                "Dom Rosa" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_5e67b30f-dec7-4aff-bdca-0f86296e55b2.png?v=1775248113&width=3840&quality=75",
                "Liquide Gold" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_f856f6b4-c483-4cee-88ed-7aaa691c1d82.png?v=1775248115&width=3840&quality=75",
                "Tumultu" to "https://hparfums.com/cdn/shop/files/TUMULTU_-_Liquides_Imaginaires_H_Parfums_Canada.png?v=1751818125&width=2048",
                "Bloody Wood" to "https://cdn4.beautinow.com/wp-content/uploads/2023/08/Liquides-Imaginaires_Bloody-Wood-2.webp",
                "Bello Rabelo" to "https://cdn4.beautinow.com/wp-content/uploads/2023/08/Liquides-Imaginaires_Bello-Rabelo_-3.webp",
                "Ile Pourpre" to "https://cdn4.beautinow.com/wp-content/uploads/2023/08/Liquides-Imaginaires_ILE-Pourpre-2.webp",
                "Fleur de Sable" to "https://cdn4.beautinow.com/wp-content/uploads/2023/08/Liquides-Imaginaires_Fleur-de-Sable-2.webp",
            )
        )

        .addBrand(
            "Maison Crivelli", "EDP", "50,100", listOf(
                "Hibiscus Mahajád" to "https://www.my-origines.com/dw/image/v2/BJRD_PRD/on/demandware.static/-/Sites-size-master/default/dw2557b261/images/Q6012008_S4.jpg?sw=1500&sh=1500&sm=fit",
                "Oud Maracujá" to "https://befrsh.com/cdn/shop/files/maison-crivelli-oud-maracuja_800x800_9534cafd-9157-4aac-8e55-9c45ee978144.png?v=1779800927",
                "Lys Sølaberg" to "https://cdn.basler-beauty.de/out/pictures/generated/product/3/980_980_100/2677776-Maison-Crivelli-Lys-Solaberg-Eau-de-Parfum-100-ml.4c6b74b7.jpg",
                "Santals Volcanique" to "https://cdn.basler-beauty.de/out/pictures/generated/product/3/1200_1200_100/1440373-Maison-Crivelli-Santal-Volcanique-Eau-de-Parfum-30-ml.79980d66.jpg",
                "Iris Malikhan" to "https://cdn.basler-beauty.de/out/pictures/generated/product/3/1200_1200_100/2677784-Maison-Crivelli-Iris-Malikhan-Eau-de-Parfum-30-ml.c7a2b0e0.jpg",
                "Rose Saltifolie" to "https://media.niche-beauty.com/images/generated/det/75/18/maison-crivelli-rose-saltifolia.jpg",
                "Citrus Batikanga" to "https://skinlife.pt/wp-content/uploads/2677792-Maison-Crivelli-Citrus-BatiKanga-Eau-de-Parfum-100-ml.adc55e25.jpg",
                "Absinthe Boréale" to "https://www.scentamor.de/cdn/shop/files/Maison_Crivelli_Absinthe_Boreale_scentamor_2.png?v=1751382345&width=1080",
                "Papyrus Moléculaire" to "https://parfumerie-opera-bordeaux.fr/wp-content/uploads/2025/05/Papyrus-Moleculaire-6.png",
            )
        )

        .addBrand(
            "Mind Games", "EDP", "100", listOf(
                "Grand Master" to "https://parfumexquis.com/cdn/shop/files/GrandMasterMindGames_4.jpg?v=1749244621&width=1400",
                "Double Attack" to "https://imgcdn.scentbird.com/_/rt:fill/w:3840/fq:jpg:85:avif:70:webp:85/bg:fff/f:jpg/c2NlbnRiaXJkL3Byb2R1Y3QtaW5mb3JtYXRpb24tbWFuYWdlbWVudC9pbWctMTc0MjI1MzY1MDA2NC5qcGc=",
                "J'Adoube" to "https://www.mindgamesfragrance.com/cdn/shop/files/MG_INDIVIDUAL_JADOUBE.jpg?v=1750278962",
                "Checkmate" to "https://www.scentamor.de/cdn/shop/files/23_938676c8-ddee-4162-87e4-0780b1cb4a08.png?v=1751382188&width=1080",
                "Caissa" to "https://perfumedefrance.com/cdn/shop/products/Caissa_960x_crop_center_c79bdf6d-c5a3-4911-bc13-b50066804f36_800x.webp?v=1666836825",
                "Castling" to "https://www.mindgamesfragrance.com/cdn/shop/files/MG_INDIVIDUAL_CASTLING.jpg?v=1750299858",
                "Scholars Mate" to "https://parfumexquis.com/cdn/shop/files/Scholars_sMateMindGames_3.jpg?v=1752257608&width=1400",
                "Blockade" to "https://www.mindgamesfragrance.com/cdn/shop/files/MG_INDIVIDUAL_BLOCKADE.jpg?v=1750342436",
                "The Forward" to "https://so-avant-garde.com/cdn/shop/files/TheForward.jpg?v=1691016921&width=1946",
                "Queening" to "https://www.marcgebauer.com/cdn/shop/files/Queening4_1024x.jpg?v=1774462631",
            )
        )

        .addBrand(
            "Nasomatto", "EDP", "30", listOf(
                "Black Afgano" to "https://media.cdn.kaufland.de/product-images/1024x1024/1bea389976988c295c5255402b3cccb5.jpg",
                "Baraonda" to "https://mygodshot.com/wp-content/uploads/2024/01/IMG_4814.jpeg",
                "Pardon" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_0cd0dede-11f1-4893-b55b-5d122db8ff9d.png?v=1775225193&width=3840&quality=75",
                "Duro" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_6b5da8fe-fbde-4d6f-b952-a3c351b92e46.png?v=1775225191&width=3840&quality=75",
                "Blamage" to "https://www.saison.com.au/cdn/shop/products/nasomatto-blamage-parfum-extrait-403767486.jpg?v=1768028048&width=1424",
                "Silver Musk" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_c2d09586-bd8b-4320-a5cd-f1bafe09792e.png?v=1775225196&width=3840&quality=75",
                "Absinth" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSsmOhzGGD8-M8zxEC0z9S83e-WNHtXhK66wsh_0-JQ7Y2ZU_M6xvsOd0Q1&s=10",
                "Narcotic V." to "https://galleriagraffiti.com/cdn/shop/files/Image_34583_3.jpg?v=1761355211&width=1050",
                "Fantomas" to "https://visionaryfragranceseu.com/cdn/shop/files/0O9A1005Large.jpg?v=1695485804",
                "Sadonaso" to "https://cdn.50-ml.media/media/catalog/product/n/a/nasomatto_sadonaso_30_ml_4.jpg?optimize=medium&bg-color=255,255,255&fit=bounds&height=540&width=540",
            )
        )

        .addBrand(
            "Nishane", "EDP", "15,50,100", listOf(
                "Hacivat" to "https://parfumexquis.com/cdn/shop/files/HacivatNishane_4.jpg?v=1747926312&width=1400",
                "Ani" to "https://thescentnest.com/wp-content/uploads/2025/08/ani-nishane-1.webp",
                "Wulong Cha" to "https://www.almaycasa.com/cdn/shop/files/NISHANE_Wulong_Cha_100ml_Extrait_De_Parfum_03.jpg?v=1776616241&width=1080",
                "Fan Your Flames" to "https://media.zid.store/bfaa3454-0e90-4f91-91c7-be4cb1752ac8/bccf34d4-6c52-4f3d-83f6-f24a60bbc5db.jpg",
                "Hundred Silent Ways" to "https://cdn.notinoimg.com/detail_main_lq/nishane/8683608071041_03/hundred-silent-ways-x___250411.jpg",
                "Ege / ΑΙΓΑΙΟ" to "https://www.libertineparfumerie.com.au/cdn/shop/files/Libertine-Parfumerie-lifestyle-Nishane-EGE.webp?v=1733447950&width=1200",
                "Karagoz" to "https://otroperfume.com/cdn/shop/products/nishane-karagoz-istanbul-cima.jpg?v=1699606956&width=1946",
                "Nefs" to "https://perfumehouse.ae/cdn/shop/files/img_3532_08ab633a-1f3c-4899-a7ce-41df62ff6b08.jpg?v=1770105682&width=1080",
                "Hacivat X" to "https://cdn4.beautinow.com/wp-content/uploads/2024/05/2-2.webp",
                "Ambra Calabria" to "https://cdn4.beautinow.com/wp-content/uploads/2024/02/Ambra-Calabria-Extrait-de-Parfum-1.jpg",
            )
        )

        .addBrand(
            "Orto Parisi", "EDP", "50", listOf(
                "Megamare" to "https://ortoparisi.com/cdn/shop/files/OrtoParisi-Megamare-1080x1350-04.gif?v=1764081136",
                "Terroni" to "https://ortoparisi.com/cdn/shop/files/OrtoParisi-Terroni-1080x1350-02REV.gif?v=1764081165",
                "Boccanera" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_c5b05c0f-3f93-4e46-a4fd-1e18dea3549d.png?v=1775225190&width=3840&quality=75",
                "Stercus" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTQixjJn3IwAfqexe4bmz8CsuBMx0-9br0qEhSzLy92_eLjoxsaWfcfxJ8&s=10",
                "Brutus" to "https://cdn.50-ml.media/media/catalog/product/o/r/orto_parisi_brutus_4.jpg?optimize=medium&bg-color=255,255,255&fit=bounds&height=540&width=540",
                "Bergamask" to "https://befrsh.com/cdn/shop/files/orto-parisi-bergamask_800x800_0bf873f5-3bfe-4883-b500-2e2cae1f797e.png?v=1779800611",
                "Viride" to "https://www.perfumelounge.eu/_next/image?url=https%3A%2F%2Fmedia.perfumelounge.eu%2Ffile%2Fperfumelounge%2Fperfumelounge%2Foriginal_images%2FOrto_Parisi_-_Viride_YIoZMZ5.jpg&w=3840&q=75",
                "Seminalis" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRShvxspzH5B9ECYhzvwZ86MdTsKGAXNFQn7Mnek2pSumYT2_K0KIVz0vA&s=10",
                "Cuoium" to "https://ortoparisi.com/cdn/shop/files/OrtoParisi-Cuoium-1080x1350-02REV.gif?v=1764081111",
            )
        )

        .addBrand(
            "Pana Dora", "EDP", "100", listOf(
                "Imperial Wood" to "https://www.pieper.de/media/image/e6/ea/8c/Imperial-wood-3_600x600@2x.jpg",
                "Swedish Wood" to "https://www.scentamor.de/cdn/shop/files/13_fd88db7c-67b9-4e6c-8d5a-6919ab73f860.png?v=1751381808&width=1080",
                "Velvet Iris" to "https://media.parfumo.com/perfume_imagery/15/156627-velvet-iris-pana-dora_1200.jpg",
                "Moonlight" to "https://lovecareskin.de/wp-content/uploads/2024/02/products-Pana-Dora_Moonlight_100-ml_Mood.webp",
                "Kropp & Själ" to "https://aromi-ulm.de/cdn/shop/files/K3.jpg?v=1745763442&width=2001",
                "Oud Republic" to "https://www.essence-garden.de/cdn/shop/files/oud-republic_notes.jpg?v=1772831727&width=2000",
                "Grand Musk" to "https://panadora.se/cdn/shop/files/Grand_Musk_-_Story-2.jpg?v=1763457181&width=1400",
                "Aqua de Dora" to "https://aromi-ulm.de/cdn/shop/files/A3.jpg?v=1745764239&width=2001",
            )
        )

        .addBrand(
            "Parfums de Marley", "EDP", "75,125,200", listOf(
                "Layton" to "https://www.beautytheshop.com/imgs/productos_cosmetica/resized/680x680/3700578502322_3.webp",
                "Herod" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_38764a82-05e3-4699-928c-39f33b8893d6.png?v=1775248316&width=3840&quality=75",
                "Percival" to "https://cdn2.jomashop.com/media/catalog/product/cache/b3e31d40bbb1abcc90b26106659d5d3f/p/a/parfums-de-marly-mens-percival-edp-spray-42-oz-125-ml-3700578523006_3.jpg?width=800&height=800",
                "Pegasus" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_b75b5edc-50c0-47d3-9a9b-969c9490bbd3.png?v=1775248322&width=3840&quality=75",
                "Carlisle" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_bbd5a00e-066f-4234-b272-2afbfdd29bcb.png?v=1775248311&width=3840&quality=75",
                "Greenley" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTOZ5uM5bDf7jCe35JlKfPWq7diECUnP4V4t41yZnCcYHe7195RTFYj_2Q&s=10",
                "Sedley" to "https://bluemercury.com/cdn/shop/files/global_images-p3700578502186-1_6244b6f5-6985-42ac-91a0-a09f38d8c5cf.jpg?v=1775753706&width=1500",
                "Oajan" to "https://befrsh.com/cdn/shop/files/parfums-de-marly-oajan_800x800_db368313-7f6c-45a7-8007-36c74ab36072.png?v=1779800208",
                "Haltane" to "https://cdn4.beautinow.com/wp-content/uploads/2023/08/Haltane-Eau-de-Parfum-125ml-1-1.webp",
                "Althaïr" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_fb3f74ad-0987-44cc-961c-744f53bb56c0.png?v=1775248312&width=3840&quality=75",
            )
        )

        .addBrand(
            "Reinvented", "EDP", "75", listOf(
                "Sacred Bond" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_e4e52eee-4aff-4da6-bf2f-81c253fc5c97.png?v=1768636512&width=3840&quality=75",
                "Lucid Dream" to "https://puapua.de/cdn/shop/files/lucid-dream-9509817.jpg?v=1752045346&width=1080",
                "Aether Aura" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_aadd8c85-f5ce-4931-abdd-2284b9d07a2d.png?v=1781944808&width=3840&quality=75",
                "Pheromones" to "https://parfumexquis.com/cdn/shop/files/PheromonesReinvented_5.jpg?v=1734536413&width=1400",
                "Illusion" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR7UAgGNQlvgNdHiWoTAAF8hTbCThVBa7ba6pBwq8qG-QlhXftwgD8-Rpc&s=10",
            )
        )

        .addBrand(
            "Roberto Ugolini", "EDP", "100", listOf(
                "17 Rosso" to "https://www.süsskind.de/media/image/product/64/lg/17-rosso-abfuellung-2ml-von-roberto-ugolini.jpg",
                "Marzocco" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ76sKHVU2LCVTTCygxTdhYgcJ5DMvM6Rq9s7GYLCbr9OmKJwNG_tqMHjs&s=10",
                "Azzurro" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_6b44e6e2-ad4b-4895-88b8-2128f78847aa.png?v=1775248389",
                "Blue Suede Shoes" to "https://cdn.shopify.com/s/files/1/0867/4204/0925/files/scene_d16211f1-c402-49bf-9bd3-89a4ecf17855.png?v=1775248390",
                "Oxford" to "https://www.essence-garden.de/cdn/shop/files/oxford-roberto-ugolini-parfum-7029680.jpg?v=1764690917",
            )
        )

        .addBrand(
            "Roja Parfums", "EDP", "100", listOf(
                "Elysium Pour Homme" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRFDJ_GAOnLh3QBBJI6klKOC1v2oPT9UCx8GgPPsQ2A6wMO_MRfWh4VdvsA&s=10",
                "Enigma Pour Homme" to "https://cdn.notinoimg.com/detail_main_lq/roja-parfums/5060370916955_07/enigma-parfum-cologne___240723.jpg",
                "Apex" to "https://www.4doutfitters.com/cdn/shop/files/Roja-Parfums_Cannes-2024_Apex-Intense_1_9x16_7e074ca9-d3f8-42d0-92ad-ff88d2f49515.jpg?v=1740653736&width=1080",
                "Oceania" to "https://rojalondon.com/cdn/shop/files/oceania-travel-spray-roja-parfums-367571.jpg?v=1780925521&width=1080",
                "Scandal Pour Homme" to "https://hrd-live.cdn.scayle.cloud/images/c5493546b1fe50bb1215318249af0ea8.jpg?brightness=1&width=922&height=1230&quality=75&bg=ffffff",
                "Danger Pour Homme" to "https://cdn.notinoimg.com/detail_main_lq/roja-parfums/5060370916924_03/danger-pour-homme___240723.jpg",
                "Midsummer Dream" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQbkl4m22L5hptDOHxw19_jVnUO17OWH-kQB7tNpYKtpWmOMGMYhj8A223T&s=10",
                "Burlington 1819" to "https://www.libertineparfumerie.com.au/cdn/shop/files/Burlington.png?v=1689730172&width=1500",
                "Sweetie Aoud" to "https://rojalondon.com/cdn/shop/files/sweetie-aoud-parfum-roja-parfums-153590.jpg?v=1753372888&width=360",
                "Manhattan" to "https://cdn.notinoimg.com/detail_main_lq/roja-parfums/5056002603935_09/manhattan___240723.jpg",
            )
        )

        .addBrand(
            "Simone Andreoli", "EDP", "100", listOf(
                "Malibu - Party in the Bay" to "https://simoneandreoli.com/wp-content/uploads/2024/03/malibu.webp",
                "Leisure in Paradise" to "https://www.4doutfitters.com/cdn/shop/files/leisure-in-paradise.webp?v=1755811929&width=1200",
                "Pacific Park" to "https://simoneandreoli.com/wp-content/uploads/2024/07/pacific-park.webp",
                "Don't Ask Me Permission" to "https://simoneandreoli.com/wp-content/uploads/2024/07/dont-ask-me-permission.webp",
                "Sunplosion" to "https://simoneandreoli.com/wp-content/uploads/2024/07/sunplosion.webp",
                "Smoke of Desert" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQtYmcnqB-PCLEnZpUGQs3lrXRrUB3QxzMrkHMr9Y_aB-hefN-XOfyYSxg&s=10",
                "Ocean of a Midnight Moon" to "https://simoneandreoli.com/wp-content/uploads/2024/07/ocean-of-a-midnight-moon.webp",
            )
        )

        .addBrand(
            "Sospiro", "EDP", "100", listOf(
                "Il Padrino" to "https://www.süsskind.de/media/image/product/1540/lg/il-padrino-probe-abfuellung-2ml-von-sospiro.jpg",
                "Vibrato" to "https://zgoperfumery.com/cdn/shop/articles/Vibrato_half_page_Print_2_c1c0c5f8-0abd-4d6f-851e-33617ad773ed.jpg?crop=center&height=1200&v=1753205027&width=1200",
            )
        )

        .addBrand(
            "The Nose Behind", "EDP", "100", listOf(
                "Kobe Gardens" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQE99fxLDuA2uq40S3y2r91xy1aycP3_P2XV2_VzopG2Vftmi6AlhfXsuw1&s=10",
                "Miami South Beach" to "https://www.marcgebauer.com/cdn/shop/files/1_bf1218af-8946-452e-aa1d-fabf252688d1_900x.jpg?v=1708333183",
                "Marrakech" to "https://media.parfumo.com/user_imagery/8e/8e_319d6a68c696606b57f480639506512a932e9bad_1200.jpg",
                "Tennis Green" to "https://nosebehind.com/cdn/shop/files/Tennis_Green__1000x1000_DSC0111_621b35d4-fdaf-465a-b0ff-7f21e57addba.jpg?v=1738771944&width=1500",
            )
        )

        .addBrand(
            "Prada", "EDP", "50,100,150", listOf(
                "L'Homme" to "https://www.scento.com/_next/image?url=https%3A%2F%2Fimagedelivery.net%2FdTnWiVKich4PS2lEWud18g%2F6a26a30f-73ee-405d-389e-9f1322b7e200%2Fcampaign&w=3840&q=75",
                "Luna Rossa Black" to "https://cdn.notinoimg.com/detail_main_lq/prada/8435137782949_04_/luna-rossa-black___250806.jpg",
                "Luna Rossa Ocean" to "https://cdn.notinoimg.com/detail_main_lq/prada/3614273556620_02_/luna-rossa-ocean___250808.jpg",
                "Paradigme" to "https://www.lookfantastic.at/images?url=https://static.thcdn.com/productimg/original/16962114-8525256701087244.jpg&format=webp&auto=avif&width=1200&height=1200&fit=cover",
            )
        )

        .addBrand(
            "Tom Ford", "EDP", "30,50,100", listOf(
                "Oud Wood" to "https://cdn.basler-beauty.de/out/pictures/generated/product/2/980_980_100/1604996-TOM-FORD-Oud-Wood-Eau-de-Parfum-30-ml.58f47ae9.jpg",
                "Lost Cherry" to "https://cdn.basler-beauty.de/out/pictures/generated/product/2/980_980_100/2611325-TOM-FORD-Lost-Cherry-Eau-de-Parfum-30-ml.54463e63.jpg",
                "Tobacco Vanille" to "https://cdn.basler-beauty.de/out/pictures/generated/product/2/1200_1200_100/2611155-TOM-FORD-Tobacco-Vanille-Eau-de-Parfum-50-ml.fa872886.jpg",
                "Tuscan Leather" to "https://www.lookfantastic.at/images?url=https://static.thcdn.com/productimg/original/14579419-1495094186739581.jpg&format=webp&auto=avif&width=1200&height=1200&fit=cover",
                "Noir Extreme" to "https://cdn.basler-beauty.de/out/pictures/generated/product/3/1200_1200_100/1602160-TOM-FORD-Noir-Extreme-Eau-de-Parfum-100-ml.ba4572af.jpg",
                "Ombre Leather" to "https://cdn.notinoimg.com/detail_main_lq/tom-ford/888066075138_03/ombre-leather___230626.jpg",
                "Grey Vetiver" to "https://cdn.notinoimg.com/detail_main_lq/tom-ford/888066007795_03/grey-vetiver___211025.jpg",
                "Fucking Fabulous" to "https://cdn.basler-beauty.de/out/pictures/generated/product/2/980_980_100/2611287-TOM-FORD-Fucking-Fabulous-Eau-de-Parfum-30-ml.87ba1c4b.jpg",
                "Bitter Peach" to "https://static.thcdn.com/productimg/original/13133995-3404895131731819.jpg",
                "Costa Azzurra" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRQOd0R1KfRyyxmZ-bXM4CjVPVlWptdGVjiBiuxqfOs1uVqcYazyXX1sDg&s=10",
                "Vanilla S*x" to "https://cdn.notinoimg.com/detail_main_lq/tom-ford/888066139724_03/private-blend-vanilla-sex___250305.jpg",
            )
        )

        .addBrand(
            "Widian", "EDP", "50,100", listOf(
                "London" to "https://cdn4.beautinow.com/wp-content/uploads/2025/09/WIDIAN_London_Eau-de-Parfum-2.webp",
                "New York" to "https://www.essence-garden.de/cdn/shop/files/new-york-widian-parfum-3062939.jpg?v=1764690616",
                "Liwa" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR9e0igImdOWYdO-khOPgNWqlI5JSRf4W2OhcQ7gIMjdiYcCHJ93iSVsbB2&s=10",
                "Delma" to "https://media.parfumo.com/perfume_imagery/cb/cb5f29-delma-widian-aj-arabia_1200.jpg",
                "Baniyas" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRIfyS_dAYpa5IQumWuPnVzvhoYnPjzHwwFQbiRTMG4VkmlLI5Q1Y43rLxU&s=10",
                "Aswan" to "https://www.essence-garden.de/cdn/shop/files/aswan-widian-parfum-1716888.jpg?v=1764690616",
                "Al Wasl" to "https://www.widian.com/wp-content/uploads/2023/07/Expo-Al-Wasl-Lifestyle.jpg",
            )
        )

        .addBrand(
            "Xerjoff", "EDP", "50,100", listOf(
                "Naxos" to "https://befrsh.com/cdn/shop/files/xerjoff-naxos_800x800_4b108f31-d443-4afa-b3a6-c4150b86593d.png?v=1779800156",
                "Torino21" to "https://zgoperfumery.com/cdn/shop/files/xerjoff-xerjoff-torino21-eau-de-parfum__79145.1692051179.1280.1280.jpg?v=1754870364&width=1280",
                "Erba Pura" to "https://pafory.com/_next/image?url=https%3A%2F%2Fpafory.com%2Fwp-content%2Fuploads%2F2019%2F12%2FBildschirmfoto-2025-02-17-um-10.34.48.webp&w=3840&q=100",
                "Alexandria II" to "https://pafory.com/_next/image?url=https%3A%2F%2Fpafory.com%2Fwp-content%2Fuploads%2F2020%2F05%2FAlexandria-II.webp&w=3840&q=100",
                "Torino22" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSa6-i6zuvwdTfklWcDIJXN1prWKK18Vc2J6qpv2bcTLH1ADAhEwGj-7dE&s=10",
                "Renaissance" to "https://pafory.com/_next/image?url=https%3A%2F%2Fpafory.com%2Fwp-content%2Fuploads%2F2019%2F12%2FBildschirmfoto-2025-02-17-um-10.35.21.webp&w=3840&q=100",
                "Accento" to "https://www.scentsangel.com/cdn/shop/files/UP8057685640016_NOTAS_66_1_750x.webp?v=1744896910",
                "Uden" to "https://www.xerjoff.com/cdn/shop/files/gallery-xerjoff-uden-eau-de-parfum-50ml_1.jpg?v=1770197523&width=1946",
                "Monkey Special (Tony Iommi)" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQZ4HEjdhBriOKH_GaaKIQjFMdsV-1-lYUkQbaEpGJ0MacL0mldgTsUq02_&s=10",
                "Newcleus" to "https://www.xerjoff.com/cdn/shop/files/gallery-xerjoff-sans-still.jpg?v=1747743534&width=1946",
            )
        )

        .addBrand(
            "Georgio Armani", "EDP", "30,50,100,150", listOf(
                "Acqua di Giò Profondo" to "https://i1.perfumesclub.com/grande/195686-4.jpg",
                "Acqua di Giò Parfum" to "https://cdn.notinoimg.com/detail_main_lq/armani/3614273954167_05/acqua-di-gio-parfum___240325.jpg",
                "Armani Code Parfum" to "https://i1.perfumesclub.com/grande/174742-3.jpg",
                "Stronger With You Intensely" to "https://www.faces.sa/dw/image/v2/BJSM_PRD/on/demandware.static/-/Sites-faces-master-catalog/default/dw55c236b4/images/001717204664_3.jpg?sw=800&sh=800",
                "Stronger With You Absolutely" to "https://www.parfuemerie-kilb.de/out/pictures/generated/product/5/420_420_90/3614273335812-5.jpg",
                "Acqua di Giò EdT" to "https://cdn.notinoimg.com/detail_main_lq/armani/3614273955546_08/acqua-di-gio___240307.jpg",
            )
        )

        .addBrand(
            "Hugo Boss", "EDP", "30,50,75,100,150,200", listOf(
                "Boss Bottled EdT" to "https://i1.perfumesclub.com/grande/23339-3.jpg",
                "Boss Bottled Eau de Parfum" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTIHmhI4omMHNlHm60hYWbaTqsPdZIo4vg-gdPJpFh681f6ETJRtetgcWb6&s=10",
                "Boss The Scent Elixir" to "https://media.parfumo.com/perfume_imagery/37/377308-the-scent-elixir-for-him-hugo-boss_1200.jpg",
            )
        )

        .addBrand(
            "Yves Saint Laurent", "EDP", "60,100,150,200", listOf(
                "La Nuit de L'Homme EdT" to "https://media.parfumo.com/user_imagery/43/43_ad72838eb2019fb6a94d24200117fc2e8f4a268b_1200.jpg",
                "Y Eau de Parfum" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR_PGONPStwZrxn589kG3eVWSPsefC_LrfCTn4eINv4Xp_xyvnESKclPL5z&s=10",
                "Y Le Parfum" to "https://media.cdn.kaufland.de/product-images/1024x1024/779d3fbfaa7c580635b170eeff6ddaec.jpg",
                "Tuxedo" to "https://cdn.sheeel.com/catalog/product/cache/074f467fdf747a38ab5e8f88243fd86f/1/2/125ml-tuxedo-smt_1_.jpg",
                "Babycat" to "https://media.parfumo.com/perfume_imagery/7d/7d94be-le-vestiaire-babycat-yves-saint-laurent_1200.jpg",
                "Myslf EdP" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQpMifRLobAI3YpCOz8TXsbC5W8vFJCORS59SIfJ24xEW34ecwMSJoECIM&s=10",
                "Caban" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT3Cn4wh7Gnb9JAkbLwvjvdVdBeogWvLxCohmx40Bn3RL5EWJWKLRQ1JNU&s=10",
                "Y Elixir" to "https://cdn.basler-beauty.de/out/pictures/generated/product/3/1200_1200_100/2633868-Yves-Saint-Laurent-Y-L-Elixir-60-ml.0419ac21.jpg",
            )
        )

        .addBrand(
            "Amouage", "EDP", "100", listOf(
                "Reflection Man" to "https://cdn.flaconi.net/media/catalog/product/7/0/701666410706_c_live.jpg?r=1WAHKF&c=at&w=1200&q=80",
                "Interlude Man" to "https://cdn.basler-beauty.de/out/pictures/generated/product/5/1200_1200_100/1455346-AMOUAGE-Iconic-Interlude-Man-Eau-de-Parfum-100-ml.f9f56a95.jpg",
                "Jubilation XXV Man" to "https://hiddensamples.com/cdn/shop/files/amouage-jubilation-xxv-fragrance-for-men.jpg?v=1761321165&width=1445",
                "Beach Hut Man" to "https://fimgs.net/mdimg/vijestide/o.13097.2.jpg",
                "Enclave" to "https://www.ausliebezumduft.de/cdn/shop/files/enclavemood1.png?v=1744178373",
                "Meander" to "https://www.aedes.com/cdn/shop/products/MEANDER2-AMOUAGE.jpg?v=1604089459&width=800",
                "Guidance" to "https://hrd-live.cdn.scayle.cloud/images/bdfadb8604c6390ca50a1ea7d78b571b.jpg?brightness=1&width=922&height=1230&quality=75&bg=ffffff",
                "Royal Tobacco" to "https://cdn.riah.ae/storage/upload/images/2024/02/05/65c0a541f2bb3.jpg",
                "Lyric Man" to "https://cdn.notinoimg.com/detail_main_lq/amouage/701666311911_03/lyric___220503.jpg",
                "Reflection 45" to "https://cdn.flaconi.net/media/catalog/product/7/0/701666410706_c_live.jpg?r=1WAHKF&c=at&w=1200&q=80",
            )
        )

        .addBrand(
            "Azzaro", "EDP", "50,100,150", listOf(
                "Wanted by Night" to "https://cdn.notinoimg.com/detail_main_lq/azzaro/3351500009848_03/wanted-by-night___221130.jpg",
                "The Most Wanted EdP Intense" to "https://i.makeupstore.at/k/kd/kdhnzsvmju6b.jpg",
                "The Most Wanted Parfum" to "https://i.makeupstore.at/m/md/mdew00j5q5kt.jpg",
                "Chrome" to "https://www.chemistwarehouse.com.au/_next/image?url=https%3A%2F%2Fstatic.chemistwarehouse.com.au%2Fams%2Fmedia%2Fpi%2F50739%2FADD4_800.jpg&w=3840&q=75",
                "Chrome Extreme" to "https://www.myperfumeshop.qa/cdn/shop/products/azzaro-chrome-extreme-edp-perfume-cologne-848169.webp?v=1705003347&width=779",
                "Wanted EdT" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQJgiMExuhgGINmaLQZjvKRBvbu3G4FvqN-rGqE5KyVvrpoSb9R-pGmsCcU&s=10",
                "Chrome Pure" to "https://cdn.notinoimg.com/detail_main_lq/azzaro/3351500005475_02/chrome-pure___221021.jpg",
                "Wild Mint" to "https://media.parfumo.com/user_imagery/71/71_5ccacd2085c7cc80b36a4f01119d27b27a17e436_1200.jpg",
            )
        )

        .addBrand(
            "Byredo", "EDP", "50,100", listOf(
                "Gypsy Water" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRnv44K9JSHfCkgjQ_xfqAL2wGyG2Qs7Ij1uHpoLtV5RbaNgldv3uHOPI0k&s=10",
                "Bal d'Afrique" to "https://www.süsskind.de/media/image/product/150/md/bal-dafrique-abfuellung-2ml-von-byredo.jpg",
                "Mojave Ghost" to "https://www.woodberg.de/cdn/shop/files/Byredo-mojave-ghost-absolue-50ml-02.jpg?v=1727714789&width=1000",
                "Blanche" to "https://www.woodberg.de/cdn/shop/files/Byredo-blanche-absolu-50ml-02.jpg?v=1741275254&width=1000",
                "Black Saffron" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ8odRkPVhAeJCh2Q_MCX8UDNLENOGTRo8HiJR25HQ7bwilc8rPFm-MuZxB&s=10",
                "Super Cedar" to "https://www.scentsangel.com/cdn/shop/files/Futura_-_2024-10-18T102844.641.png?v=1742242230&width=1100",
                "Oud Immortel" to "https://galleriagraffiti.com/cdn/shop/files/Image_34755_3.jpg?v=1761355236&width=1050",
                "Mixed Emotions" to "https://bs24.swiss/cdn/shop/files/Screenshot_2024-12-02_233003.png?v=1733169832",
                "Eyes Closed" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQlQ8Jx2FBWxkqtKw_6FskApNHB5woEt4oyZ_jZq9hsIo0ZNkgoq7l9wxU&s=10",
                "Rouge Chaotique" to "https://mir-s3-cdn-cf.behance.net/projects/404/abe901199458201.Y3JvcCwyMTIyLDE2NjAsNDE1LDA.jpg",
            )
        )

        .addBrand(
            "Clive Christian", "EDP", "50", listOf(
                "X Pour Homme" to "https://m.media-amazon.com/images/I/717V5-ZWa1L.jpg",
                "1872 Pour Homme" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQr1WP9opt6anFrL1nIljQSp5Z3AvjNmhtxJ0tpgm9bP9rOLY_LcVBRu77U&s=10",
                "No. 1 Masculine" to "https://us.clivechristian.com/cdn/shop/products/CC-NO1P50RM01_3.jpg?v=1757886688&width=1946",
                "Blonde Amber" to "https://rabica.ca/cdn/shop/files/clive-christian-xxi-art-deco-blonde-amber-2364411.jpg?v=1776156137&width=2000",
                "L Floral Chypres" to "https://www.myperfumeshop.qa/cdn/shop/products/clive-christian-private-collection-l-floral-chypre-perfume-perfume-cologne-171702.jpg?v=1747831002&width=1000",
                "I Amber Oriental" to "https://www.myperfumeshop.qa/cdn/shop/products/clive-christian-private-collection-i-amber-oriental-perfume-perfume-cologne-693734.jpg?v=1702710932&width=1000",
                "Town & Country" to "https://www.beautytheshop.com/imgs/productos_cosmetica/resized/680x680/0652638012278_7.webp",
                "Crab Apple Blossom" to "https://aromi-ulm.de/cdn/shop/files/clive_christian_crab_apple_blossom_extrait_de_parfum_1.webp?v=1777547977&width=1000",
                "Matsukita" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTjo0ZS3GrEsNjqB3M7C3s9gLP58COreb2Sf_Hw14DZcbocGP3Y2AYn8j8&s=10",
                "Jump Up and Kiss Me Hedonistic" to "https://parfumexquis.com/cdn/shop/files/JumpUpandKissMeHedonisticCliveChristian_7.jpg?v=1739991192&width=1400",
            )
        )

        .addBrand(
            "Creed", "EDP", "30,50,75,100,240", listOf(
                "Aventus" to "https://www.beautytheshop.com/imgs/productos_cosmetica/resized/680x680/3508440505118_3.webp",
                "Green Irish Tweed" to "https://fimgs.net/himg/o.R9SImAhFirh.png",
                "Silver Mountain Water" to "https://cdn.sheeel.com/catalog/product/cache/074f467fdf747a38ab5e8f88243fd86f/c/r/creed_silver_mountain_water_100ml_edp_for_men5.jpg",
                "Viking Cologne" to "https://cdn.basler-beauty.de/out/pictures/generated/product/5/1200_1200_100/2510103-Creed-Viking-Cologne-Eau-de-Parfum-50-ml.bc989420.jpg",
                "Original Santal" to "https://www.myperfumeshop.co.nz/cdn/shop/products/creed-creed-original-santal-edp-565797.jpg?v=1609925204&width=720",
                "Himalaya" to "https://levelperfume.com/cdn/shop/files/Untitled-1_ba7b670e-c9ac-4c7a-ba5b-d21e43a5a174_1200x1200.jpg?v=1715148512",
                "Virgin Island Water" to "https://atlashomme.com.au/cdn/shop/files/Untitled_design_-_2023-10-06T155817_902_png_1400x.webp?v=1729214315",
                "Aventus Cologne" to "https://creedboutique.com/cdn/shop/files/PDP-Image-Carousel-Aventus-Cologne-flatlay-1x1_2.jpg?v=1779136159&width=375",
                "Absolu Aventus" to "https://www.creedfragrances.co.uk/cdn/shop/files/PDP-Image-Carousel-Absolu-Aventus-flatlay-1x1_2.jpg?v=1779172845&width=375",
            )
        )

        .addBrand(
            "Dior", "EDP", "50,100,150", listOf(
                "Sauvage EdT" to "https://www.dior.com/dw/image/v2/BGXS_PRD/on/demandware.static/-/Library-Sites-DiorSharedLibrary/default/dwf80880b6/images/beauty/01-FRAGRANCES/2025/PDP-REVAMP/SAUVAGE/Y0785220/POSTER_DIOR_SAUVAGE_CAPSULE_EDP_VA_916.jpg?sw=800",
                "Sauvage Eau de Parfum" to "https://www.dior.com/dw/image/v2/BGXS_PRD/on/demandware.static/-/Library-Sites-DiorSharedLibrary/default/dwa0b69d9b/images/beauty/01-FRAGRANCES/2025/PDP-REVAMP/SAUVAGE/Y0785220/POSTER_DIOR_SAUVAGE_CAPSULE_EDP_VA_169.jpg?sw=800",
                "Sauvage Elixir" to "https://www.dior.com/dw/image/v2/BGXS_PRD/on/demandware.static/-/Library-Sites-DiorSharedLibrary/default/dwfa1178fa/images/beauty/01-FRAGRANCES/2025/PDP-REVAMP/SAUVAGE/Y0996460/POSTER_CAPSULE_ELIXIR_VA_169.jpg?sw=800",
                "Homme Intense" to "https://www.dior.com/dw/image/v2/BGXS_PRD/on/demandware.static/-/Sites-master_dior/default/dw2d3e8506/Y0479201/Y0479201_F047924709_E02_ZHC.jpg?sw=1800",
                "Homme Parfum" to "https://www.dior.com/on/demandware.static/-/Sites-master_dior/default/dwf0c38445/Y0997193/Y0997193_C099700664_E02_RHC.jpg",
                "Homme Cologne" to "https://beauty-content.douglas.de/6776b6e702c9c3003b95017e/lQxQkR-douglas6.jpg",
            )
        )

        .addBrand(
            "Goldfield & Banks", "EDP", "100", listOf(
                "Pacific Rock Moss" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQtG03wfIIDZhWQbREricAkD1ncxGIgSzERXk6Cg8MTKy2VQiVy0H7Iqg_x&s=10",
                "Sunset Hour" to "https://www.beautytheshop.com/imgs/productos_cosmetica/resized/680x680/9356353000497_4.webp",
                "Wood Infusion" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcScCCY2f-0QhLH7mCqS6LCwtYCcLDVq3hOta_HFRnxQ9x3Ievy5iwcyfDk&s=10",
                "Bohemian Lime" to "https://www.beautytheshop.com/imgs/productos_cosmetica/resized/680x680/9356353000008_3.webp",
                "Desert Rosewood" to "https://www.beautytheshop.com/imgs/productos_cosmetica/resized/680x680/9369999068240_2.webp",
                "Blue Cypress" to "https://www.beautytheshop.com/imgs/productos_cosmetica/resized/680x680/9369999068233_3.webp",
                "Southern Bloom" to "https://www.beautytheshop.com/imgs/productos_cosmetica/resized/680x680/9369998021024_2.webp",
                "White Sandalwood" to "https://www.beautytheshop.com/imgs/productos_cosmetica/resized/680x680/9369999068226_1.webp",
                "Ingenious Ginger" to "https://perfumology.com/cdn/shop/files/GB_IngeniousGinger-square_1080x_dab8e8c4-999e-406a-9dcb-98fbebc2198d_1024x1024.jpg?v=1697128018",
                "Silky Woods" to "https://www.beautytheshop.com/imgs/productos_cosmetica/resized/680x680/9356353000022_2.webp",
            )
        )

        .addBrand(
            "Guerlain", "EDP", "50,100,200", listOf(
                "L'Homme Idéal EdT" to "https://www.guerlain.com/dw/image/v2/BDCZ_PRD/on/demandware.static/-/Sites-GSA_master_catalog/default/dw141d79e9/Ecrin_Image/Fragrances/LES-MASCULINS-24_SQUARE-IMAGE_PDP_KV_LHI.jpg?sw=570&sh=570",
                "L'Homme Idéal Eau de Parfum" to "https://i1.perfumesclub.com/grande/196448-3.jpg",
                "L'Homme Idéal Extreme" to "https://fimgs.net/mdimg/news/de/10694/social.10694-640x335.jpg",
                "Habit Rouge EdT" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTK8ExdWtU9pbe_JK8-v6C4lSveQWvhCno0qvyz6JnjNAsKN4oIGrOVTR2w&s=10",
                "Spiriteuse Double Vanille" to "https://www.guerlain.com/dw/image/v2/BDCZ_PRD/on/demandware.static/-/Sites-GSA_master_catalog/default/dw99e2328a/01-ProductsViewer/P017906/P017906_E01_hi-res.jpg?sw=655&sh=655",
                "Tonka Impériale" to "https://www.guerlain.com/dw/image/v2/BDCZ_PRD/on/demandware.static/-/Sites-GSA_master_catalog/default/dw7d576af2/01-ProductsViewer/P014752/P014752_E01_hi-res.jpg?sw=900&sh=900",
                "Tobacco Honey" to "https://www.guerlain.com/dw/image/v2/BDCZ_PRD/on/demandware.static/-/Sites-GSA_master_catalog/default/dw06a7e495/primary_packshot_3/2023/Fragrances/A&M/AM-TOBACCO_PRIMARY-VISUAL_PDP.jpg?sw=900&sh=900",
                "Néroli Outrenoir" to "https://www.guerlain.com/dw/image/v2/BDCZ_PRD/on/demandware.static/-/Sites-GSA_master_catalog/default/dwff9ac4b9/01-ProductsViewer/P017910/P017910_E01_hi-res.jpg?sw=570&sh=570",
                "Vetiver" to "https://www.guerlain.com/dw/image/v2/BDCZ_PRD/on/demandware.static/-/Sites-GSA_master_catalog/default/dwbf895d78/01-ProductsViewer/P030523/P030523_E03_hi-res.jpg?sw=900&sh=900",
            )
        )

        .addBrand(
            "Jean Paul Gaultier", "EDP", "75,125,200", listOf(
                "Le Male EdT" to "https://cdn.notinoimg.com/detail_main_lq/jean-paul-gaultier/8435415012669_06/le-male___221005.jpg",
                "Le Male Le Parfum" to "https://cdn.basler-beauty.de/out/pictures/generated/product/3/1200_1200_100/1681087-Jean-Paul-Gaultier-Le-Male-Le-Male-Eau-de-Parfum-Intense-75-ml.d11a9c68.jpg",
                "Le Male Elixir" to "https://media.douglas.at/medias/x9ccKl1092116-2-global.jpg?context=bWFzdGVyfGltYWdlc3w1ODA0MjR8aW1hZ2UvanBlZ3xhR0kxTDJnM09TODJNelU0T1RrMk5EY3dPVGt4T0M5NE9XTmpTMnd4TURreU1URTJYekpmWjJ4dlltRnNMbXB3Wnd8YzRlZTRkNzY4ODhjNWM3ZGIwMDIwNzEyM2MwNTlkMjJkODI3NzhiYTllOGQyMDgwYTAzNmI3NDM4NjUyMmU0Yw&grid=true",
                "Ultra Male" to "https://cdn.notinoimg.com/detail_main_lq/jean-paul-gaultier/8435415012027_05/le-male-ultra-male___221021.jpg",
                "Scandal Pour Homme" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS9wfyb-9sL56Bjma2mKqp8tqYnKtn_kT0OHJe0k28q5nxI-L2tpP_go8c6&s=10",
                "Le Beau EdT" to "https://i1.perfumesclub.com/grande/211290-4.jpg",
                "Le Beau Le Parfum" to "https://media.douglas.at/medias/nFmFQv474406-2-global.jpg?context=bWFzdGVyfGltYWdlc3w2OTUzNjZ8aW1hZ2UvanBlZ3xhRFJtTDJneU5DODJNemt6TXpVek5UZzBOalF6TUM5dVJtMUdVWFkwTnpRME1EWmZNbDluYkc5aVlXd3VhbkJufDk4ZWY0ZDM1ZDdhZjMyZjA5YmFhYjU5ZDIwZWRiNjE0NmViNjQ3ZTI3OTRmYjk2YjUyZjdlYTgxYWRiNzQ4ODI&grid=true",
                "Scandal Pour Homme Le Parfum" to "https://fimgs.net/himg/o.YbS64CgKG4U.jpg",
                "Le Beau Paradise Garden" to "https://static.fann.cz/uploads/varianty/20/44/53/20445-3.jpg",
            )
        )

        .addBrand(
            "Valentino", "EDP", "10,50,100,150", listOf(
                "Uomo EdT" to "https://www.lookfantastic.at/images?url=https://static.thcdn.com/productimg/original/12737221-1995063560160463.jpg&format=webp&auto=avif&width=1200&height=1200&fit=cover",
                "Uomo Intense" to "https://www.lookfantastic.at/images?url=https://static.thcdn.com/productimg/original/12737223-1285063560289559.jpg&format=webp&auto=avif&width=1200&height=1200&fit=cover",
                "Born In Roma Coral Fantasy" to "https://media.douglas.at/medias/4LrJIF471029-2-dgl-AT.jpg?context=bWFzdGVyfGltYWdlc3wxOTc3NjZ8aW1hZ2UvanBlZ3xhRE01TDJobU15OHpNelF5TkRVNU9ETXdNamMxTUM4MFRISktTVVkwTnpFd01qbGZNbDlrWjJ3dFFWUXVhbkJufGE5NzJjOTA4NjllOThkODY4NzFjODQwZGE2NzdjZTZhOWE1ZmQ2NDViMGM2MWY5ZDg2YzNkYjU2NTkzYzY3YjU&grid=true",
                "Born In Roma Intense" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRngCbSuzBpKBzQ3YgWi8Lu44M69KXuSghKU1WKF0Xjrg&s=10",
                "Born In Roma EdT" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSJ9bB_EH2O-uNlqeL-QurbfiLS8UwSxxglZ-SlIr2atlygg0EwW3HCK_4&s=10",
                "Born In Roma Yellow Dream" to "https://mazaya.eg/_ipx/w_1400&f_webp/https://www.mazaya.eg/media/catalog/product/v/l/vlt_dmi_frag_bir_uomo-yellow-all_ingredient_1x1_inter-en.jpg",
                "Uomo Born In Roma Green Stravaganza" to "https://cdn.notinoimg.com/detail_main_lq/valentino/3614274024807_03/born-in-roma-green-stravaganza-uomo___240111.jpg",
                "Uomo Acqua" to "https://images-static.nykaa.com/media/catalog/product/b/4/b4a2395VALAC00000016_3.jpg?tr=w-500",
            )
        )

        .addBrand(
            "Chanel", "EDP", "35,50,100,200", listOf(
                "Bleu de Chanel EdP" to "https://img.kingpowerclick.com/cdn-cgi/image/format=auto/kingpower-com/image/upload/w_640,h_640/v1768293222/prod/573160-L5.jpg",
                "Bleu de Chanel Parfum" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQyRJjkCZmY5QWl7cyCc9l3XjmWLRRWXpSgBKpdZZyRAph2tCO3X8f9QAs&s=10",
                "Allure Homme Sport" to "https://scentgrail.com/wp-content/uploads/2024/04/Chanel-Allure-Homme-Sport-cover.jpg",
                "Allure Homme Sport Eau Extrême" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQCSDBJTkbip-DS3u4JzD-9ZmeJhLSs4l2tbOYDsZcepJOUMKIrVjfnmhg&s=10",
                "Allure Homme Édition Blanche" to "https://media.douglas.at/medias/6ZWwY4359228-0-dgl-AT.jpg?context=bWFzdGVyfGltYWdlc3w1NTgwNXxpbWFnZS9qcGVnfGFHSTBMMmd6TkM4MU1URTFPREl3TVRrMU9EUXpNQzgyV2xkM1dUUXpOVGt5TWpoZk1GOWtaMnd0UVZRdWFuQm58Yzc0MzM4ZGUzY2Y1NzM4YzhhNWYxYjM5MmYwOTllZDliMzBmNjE0NTRhYzE3MmRhMzJhMmZiY2RkZjRiMTRiNw&grid=true",
                "1957" to "https://beauty.at/parfum/exclusive/Les-Exclusifs-de-Chanel-1957/Chanel-1958.jpg?v=1554047371&version=content",
            )
        )

        .addBrand(
            "Versace", "EDP", "30,50,100,200", listOf(
                "Eros EdT" to "https://img01.ztat.net/article/spp-media-p1/7ec703e94e3840109ae6c8c0b8b87c67/2fcd847a81194e7c9744e12663681d9e.jpg?imwidth=762",
                "Eros Flame" to "https://img01.ztat.net/article/spp-media-p1/bdc29f8f9cb74a48bae77d7a0c9e1684/a6e1130ce1364af393158d9855acd2ff.jpg?imwidth=762",
                "Eros Energy" to "https://img01.ztat.net/article/spp-media-p1/9b0bfc5379bc454ca0dff8cff7b29c02/32df744fad644eeb8409dffa5d0d34a6.jpg?imwidth=762",
                "Versace Pour Homme (Dylan Blue)" to "https://www.o2morny.com/cdn/shop/products/hoa-41.jpg?v=1756797563&width=1445",
                "Versace Pour Homme EdT" to "https://dimg.dillards.com/is/image/DillardsZoom/mainProduct/versace-pour-homme-eau-de-toilette-spray/00000000__03025278_01_ai.jpg",
            )
        ).addBrand(
            "Louis Vuitton", "EDP", "100,200", listOf(
                "Immensité" to "https://imagedelivery.net/dTnWiVKich4PS2lEWud18g/cb94bad9-3194-4d75-9db5-62cc9ed6bc00/campaign",
                "Imagination" to "https://www.myperfumeshop.qa/cdn/shop/files/FLACON_EXTRA_J-2_023.avif?v=1770024822&width=1967",
                "Afternoon Swim" to "https://www.fridaycharm.com/cdn/shop/files/LouisVuittonAfternoonSwim2_d191e20f-7dee-4fce-ae4d-f82c0c42fd49_1800x1800.jpg?v=1682588054",
                "Ombre Nomade" to "https://kiisasperfumes.com/cdn/shop/files/louis-vuitton-ombre-nomade-edp-100ml-533232.jpg?v=1722882208",
                "Pacific Chill" to "https://www.miniparfum.eu/cdn/shop/files/louis-vuitton-pacific-chill--LP0326_PM1_Sideview_png.jpg?v=1722454226&width=2048",
                "Météore" to "https://visionaryfragranceseu.com/cdn/shop/products/Metore_530x@2x.jpg?v=1678126535",
                "City of Stars" to "https://fimgs.net/himg/o.EDxHirn2d5I.jpg",
                "Orage" to "https://www.myperfumeshop.com.au/cdn/shop/products/louis-vuitton-orage-edp-348471.jpg?v=1700124809&width=795",
                "Symphony" to "https://www.luxafrique.boutique/cdn/shop/files/download_1_0ac1c80d-6046-46ce-a756-8875a0d83567.png?v=1758657952",
                "California Dream" to "https://persolaise.com/wp-content/uploads/2020/08/D89C618A-E141-401C-8DD8-FBBC55397882-1024x1010.jpeg"
            )
        ).addBrand(
            "Maison Margiela Replica", "EDP", "30,100", listOf(
                "Lazy Sunday Morning" to "https://fimgs.net/mdimg/secundar/o.114016.jpg"
            )
        )
        .addBrand(
            "Tiffany & Co.", "EDP", "30,50,75", listOf(
                "Tiffany & Co." to "https://epocacosmeticos.vteximg.com.br/arquivos/ids/1320669-800-800/17648426872126.jpg?v=639005512502830000"
            )

        )

    private fun List<CatalogPerfume>.addBrand(
        brand: String,
        conc: String,
        sizes: String,
        items: List<Any>
    ): List<CatalogPerfume> {
        return this + items.map { item ->
            val (name, url) = when (item) {
                is Pair<*, *> -> item.first as String to item.second as String
                else -> item as String to ""
            }
            CatalogPerfume(
                name = name,
                brand = brand,
                concentration = conc,
                availableSizes = sizes,
                imageUrl = url,
                defaultSeason = "Alle",
                defaultOccasion = "Alle"
            )
        }
    }
}
