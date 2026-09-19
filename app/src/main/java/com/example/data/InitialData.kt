package com.example.data

import com.example.data.model.*

object InitialData {

    fun getDefaultProfile(): ClassProfile {
        return ClassProfile(
            id = 1,
            className = "KELAS XI-TKJ",
            schoolName = "SMK Negeri 1",
            academicYear = "2024 / 2025",
            slogan = "Teknik Komputer & Jaringan • Solid, Cerdas, Terkoneksi",
            homeroomTeacher = "Budi Santoso, S.Kom., M.T.",
            homeroomTeacherSubject = "Wali Kelas & Guru Produktif Jaringan",
            classLeader = "Muhammad Rizaldi Pratama",
            welcomeMessage = "Selamat datang di Website Resmi KELAS XI-TKJ! Portal informasi terpadu siswa Teknik Komputer & Jaringan. Jadwal pelajaran, data 22 siswa, piket lab jaringan, transparansi kas kelas, mading tugas, dan galeri kenangan.",
            instagramHandle = "@xi_tkj.official",
            heroImageUrl = "",
            totalStudents = 22,
            maleStudents = 14,
            femaleStudents = 8,
            weeklyCashTarget = 5000L
        )
    }

    fun getDefaultStudents(): List<StudentMember> {
        return listOf(
            StudentMember(
                absentNumber = 1,
                name = "Achmad Fauzan",
                nickname = "Fauzan",
                gender = "L",
                role = "Anggota",
                nisn = "0078120001",
                quote = "Routing protokol hidup: selalu pilih jalur terbaik.",
                hobby = "Konfigurasi MikroTik & Futsal",
                dream = "Network Administrator",
                instagram = "@achmad_fauzan",
                avatarColorIndex = 0
            ),
            StudentMember(
                absentNumber = 2,
                name = "Alya Nur Safitri",
                nickname = "Alya",
                gender = "P",
                role = "Sekretaris 1",
                nisn = "0078120002",
                quote = "Catatan rapi, jaringan stabil, masa depan cerah.",
                hobby = "Menulis Notulen & Desain Grafis",
                dream = "Database Administrator",
                instagram = "@alyanursafitri",
                avatarColorIndex = 1
            ),
            StudentMember(
                absentNumber = 3,
                name = "Arya Bima Nugraha",
                nickname = "Arya",
                gender = "L",
                role = "Anggota",
                nisn = "0078120003",
                quote = "Keamanan sistem bukan opsi, tapi pondasi.",
                hobby = "Capture The Flag & Coding Python",
                dream = "Cyber Security Specialist",
                instagram = "@aryabima_tkj",
                avatarColorIndex = 2
            ),
            StudentMember(
                absentNumber = 4,
                name = "Bagas Pratama",
                nickname = "Bagas",
                gender = "L",
                role = "Anggota",
                nisn = "0078120004",
                quote = "Code never lies, comments sometimes do.",
                hobby = "Web Development & Game Online",
                dream = "Full Stack Developer",
                instagram = "@bagaspratama.tkj",
                avatarColorIndex = 3
            ),
            StudentMember(
                absentNumber = 5,
                name = "Cantika Dewi Lestari",
                nickname = "Cantika",
                gender = "P",
                role = "Bendahara 1",
                nisn = "0078120005",
                quote = "Kas lancar, praktikum aman, pertemanan tentram!",
                hobby = "Akuntansi & Menabung",
                dream = "IT Financial Auditor",
                instagram = "@cantikadewi.l",
                avatarColorIndex = 4
            ),
            StudentMember(
                absentNumber = 6,
                name = "Dimas Wahyu Ramadhan",
                nickname = "Dimas",
                gender = "L",
                role = "Anggota",
                nisn = "0078120006",
                quote = "Kabel straight atau cross, kita tetap saudara sekelas.",
                hobby = "Crimping RJ45 & Rakit PC",
                dream = "Hardware & Infrastructure Engineer",
                instagram = "@dimaswahyu.tkj",
                avatarColorIndex = 5
            ),
            StudentMember(
                absentNumber = 7,
                name = "Dinda Putri Ayu",
                nickname = "Dinda",
                gender = "P",
                role = "Sekretaris 2",
                nisn = "0078120007",
                quote = "Estetika dan fungsionalitas harus sejalan.",
                hobby = "Figma & Ilustrasi Digital",
                dream = "UI/UX Designer",
                instagram = "@dindaputriayu",
                avatarColorIndex = 0
            ),
            StudentMember(
                absentNumber = 8,
                name = "Fajar Ilham Ramadhan",
                nickname = "Fajar",
                gender = "L",
                role = "Anggota",
                nisn = "0078120008",
                quote = "Langit bukan batas, cloud adalah awal mula.",
                hobby = "Virtual Machine & Linux Server",
                dream = "Cloud Solutions Architect",
                instagram = "@fajarilham_r",
                avatarColorIndex = 1
            ),
            StudentMember(
                absentNumber = 9,
                name = "Farhan Maulana",
                nickname = "Farhan",
                gender = "L",
                role = "Wakil Ketua",
                nisn = "0078120009",
                quote = "Saling backup kawan seperti redundant server.",
                hobby = "Automasi Shell & Basket",
                dream = "DevOps Engineer",
                instagram = "@farhanmaulana.tkj",
                avatarColorIndex = 2
            ),
            StudentMember(
                absentNumber = 10,
                name = "Gilang Prasetyo",
                nickname = "Gilang",
                gender = "L",
                role = "Anggota",
                nisn = "0078120010",
                quote = "Uptime 99.9%, semangat 100%.",
                hobby = "Monitoring Jaringan & Bulutangkis",
                dream = "System Administrator",
                instagram = "@gilangprasetyo.id",
                avatarColorIndex = 3
            ),
            StudentMember(
                absentNumber = 11,
                name = "Hani Octaviana",
                nickname = "Hani",
                gender = "P",
                role = "Bendahara 2",
                nisn = "0078120011",
                quote = "Transparansi adalah kunci integritas kelas.",
                hobby = "Excel & Fotografi",
                dream = "Data Analyst",
                instagram = "@hanioctaviana_",
                avatarColorIndex = 4
            ),
            StudentMember(
                absentNumber = 12,
                name = "Irfan Hakim",
                nickname = "Irfan",
                gender = "L",
                role = "Anggota",
                nisn = "0078120012",
                quote = "Firewall hati-hati, jalan hidup teliti.",
                hobby = "Cisco Packet Tracer & Musik",
                dream = "Network Security Engineer",
                instagram = "@irfanhakim.tkj",
                avatarColorIndex = 5
            ),
            StudentMember(
                absentNumber = 13,
                name = "Kevin Jonathan",
                nickname = "Kevin",
                gender = "L",
                role = "Anggota",
                nisn = "0078120013",
                quote = "Debugging adalah seni menemukan kebenaran.",
                hobby = "Backend API & Kopi",
                dream = "Backend Developer",
                instagram = "@kevinjonathan_dev",
                avatarColorIndex = 0
            ),
            StudentMember(
                absentNumber = 14,
                name = "Laila Rahmawati",
                nickname = "Laila",
                gender = "P",
                role = "Anggota",
                nisn = "0078120014",
                quote = "Kreativitas menghubungkan ide dengan realitas.",
                hobby = "Video Editing & Content Creation",
                dream = "Digital Content Creator",
                instagram = "@lailarahmawati.id",
                avatarColorIndex = 1
            ),
            StudentMember(
                absentNumber = 15,
                name = "Muhammad Rizaldi Pratama",
                nickname = "Rizaldi",
                gender = "L",
                role = "Ketua Kelas",
                nisn = "0078120015",
                quote = "Memimpin dengan teladan, melayani dengan hati, berinovasi tanpa henti.",
                hobby = "Networking, Server Admin & Futsal",
                dream = "Chief Technology Officer (CTO)",
                instagram = "@rizaldipratama_tkj",
                avatarColorIndex = 2
            ),
            StudentMember(
                absentNumber = 16,
                name = "Nabila Syifa",
                nickname = "Nabila",
                gender = "P",
                role = "Anggota",
                nisn = "0078120016",
                quote = "Jaringan yang baik dibangun dari komunikasi yang tulus.",
                hobby = "Membaca & IoT",
                dream = "Network Architect",
                instagram = "@nabilasyifa_tkj",
                avatarColorIndex = 3
            ),
            StudentMember(
                absentNumber = 17,
                name = "Pandu Wicaksono",
                nickname = "Pandu",
                gender = "L",
                role = "Anggota",
                nisn = "0078120017",
                quote = "Penetration testing diri sendiri: kenali kelemahan dan perbaiki.",
                hobby = "Kali Linux & Renang",
                dream = "Ethical Hacker",
                instagram = "@panduwicaksono",
                avatarColorIndex = 4
            ),
            StudentMember(
                absentNumber = 18,
                name = "Rahmat Hidayat",
                nickname = "Rahmat",
                gender = "L",
                role = "Anggota",
                nisn = "0078120018",
                quote = "Open source, open mind, open heart.",
                hobby = "Debian Server & Motoran",
                dream = "Linux System Engineer",
                instagram = "@rahmathidayat.tkj",
                avatarColorIndex = 5
            ),
            StudentMember(
                absentNumber = 19,
                name = "Salma Nur Aini",
                nickname = "Salma",
                gender = "P",
                role = "Anggota",
                nisn = "0078120019",
                quote = "Responsif di semua perangkat, adaptif di semua keadaan.",
                hobby = "CSS & Merajut",
                dream = "Front-End Engineer",
                instagram = "@salmanuraini",
                avatarColorIndex = 0
            ),
            StudentMember(
                absentNumber = 20,
                name = "Tegar Aditya",
                nickname = "Tegar",
                gender = "L",
                role = "Anggota",
                nisn = "0078120020",
                quote = "Tetap dingin seperti ruang server ber-AC.",
                hobby = "Kabel Fiber Optik & Lari",
                dream = "Data Center Specialist",
                instagram = "@tegaraditya.tkj",
                avatarColorIndex = 1
            ),
            StudentMember(
                absentNumber = 21,
                name = "Vina Anggraeni",
                nickname = "Vina",
                gender = "P",
                role = "Anggota",
                nisn = "0078120021",
                quote = "Belajar algoritma kehidupan: selesaikan masalah selangkah demi selangkah.",
                hobby = "Machine Learning & Masak",
                dream = "AI Engineer",
                instagram = "@vinaanggraeni_",
                avatarColorIndex = 2
            ),
            StudentMember(
                absentNumber = 22,
                name = "Yoga Pratama",
                nickname = "Yoga",
                gender = "L",
                role = "Anggota",
                nisn = "0078120022",
                quote = "Splicing fiber optik: sambungkan potensi masa depan.",
                hobby = "Fusion Splicer & Voli",
                dream = "Fiber Optic Specialist",
                instagram = "@yogapratama.tkj",
                avatarColorIndex = 3
            )
        )
    }

    fun getDefaultSchedules(): List<ClassSchedule> {
        return listOf(
            // SENIN
            ClassSchedule(dayOfWeek = "Senin", orderNumber = 1, subject = "Upacara Bendera", timeRange = "07.00 - 07.45", teacher = "Semua Guru", room = "Lapangan Utama"),
            ClassSchedule(dayOfWeek = "Senin", orderNumber = 2, subject = "Administrasi Infrastruktur Jaringan (AIJ)", timeRange = "07.45 - 10.00", teacher = "Budi Santoso, S.Kom., M.T.", room = "Lab Jaringan 1"),
            ClassSchedule(dayOfWeek = "Senin", orderNumber = 3, subject = "Administrasi Sistem Jaringan (ASJ)", timeRange = "10.15 - 12.30", teacher = "Agus Prasetyo, S.T.", room = "Lab Server"),
            ClassSchedule(dayOfWeek = "Senin", orderNumber = 4, subject = "Pendidikan Agama & Budi Pekerti", timeRange = "13.00 - 14.30", teacher = "Drs. H. Syamsul Maarif", room = "Masjid Sekolah"),

            // SELASA
            ClassSchedule(dayOfWeek = "Selasa", orderNumber = 1, subject = "Teknologi Jaringan Berbasis Luas (WAN)", timeRange = "07.15 - 09.30", teacher = "Hendra Kurniawan, S.Kom", room = "Lab Jaringan 2"),
            ClassSchedule(dayOfWeek = "Selasa", orderNumber = 2, subject = "Teknologi Layanan Jaringan (TLJ)", timeRange = "09.45 - 12.00", teacher = "Rahmat Hidayat, M.Kom", room = "Lab VoIP & Server"),
            ClassSchedule(dayOfWeek = "Selasa", orderNumber = 3, subject = "Bahasa Inggris Komunikasi IT", timeRange = "12.45 - 14.15", teacher = "Miss Dian Anggraini, M.Pd", room = "Ruang XI-TKJ"),

            // RABU
            ClassSchedule(dayOfWeek = "Rabu", orderNumber = 1, subject = "Pendidikan Jasmani (PJOK)", timeRange = "07.15 - 08.45", teacher = "Bambang Sudiro, S.Pd", room = "Lapangan Olahraga"),
            ClassSchedule(dayOfWeek = "Rabu", orderNumber = 2, subject = "Matematika Terapan IT", timeRange = "09.00 - 10.30", teacher = "Dra. Siti Aminah, M.Si", room = "Ruang XI-TKJ"),
            ClassSchedule(dayOfWeek = "Rabu", orderNumber = 3, subject = "Produk Kreatif & Kewirausahaan (PKK)", timeRange = "10.45 - 12.45", teacher = "Ibu Sri Wahyuni, S.E., M.M", room = "Ruang Praktik PKK"),
            ClassSchedule(dayOfWeek = "Rabu", orderNumber = 4, subject = "Bahasa Indonesia", timeRange = "13.15 - 14.30", teacher = "Drs. Achmad Syafi'i", room = "Ruang XI-TKJ"),

            // KAMIS
            ClassSchedule(dayOfWeek = "Kamis", orderNumber = 1, subject = "Praktikum Fiber Optic & Splicing", timeRange = "07.15 - 09.30", teacher = "Budi Santoso, S.Kom., M.T.", room = "Lab Fiber Optic"),
            ClassSchedule(dayOfWeek = "Kamis", orderNumber = 2, subject = "Keamanan Jaringan & Cyber Security", timeRange = "09.45 - 12.00", teacher = "Agus Prasetyo, S.T.", room = "Lab Komputer 1"),
            ClassSchedule(dayOfWeek = "Kamis", orderNumber = 3, subject = "PPKn (Pendidikan Pancasila)", timeRange = "12.45 - 14.15", teacher = "Ibu Nuraini, S.Pd", room = "Ruang XI-TKJ"),

            // JUMAT
            ClassSchedule(dayOfWeek = "Jumat", orderNumber = 1, subject = "Senam Pagi & Literasi Digital", timeRange = "07.00 - 07.45", teacher = "Pembina OSIS", room = "Halaman Sekolah"),
            ClassSchedule(dayOfWeek = "Jumat", orderNumber = 2, subject = "Troubleshooting PC & Jaringan", timeRange = "07.45 - 09.30", teacher = "Hendra Kurniawan, S.Kom", room = "Bengkel Hardware"),
            ClassSchedule(dayOfWeek = "Jumat", orderNumber = 3, subject = "Bimbingan Konseling (Karier IT)", timeRange = "09.45 - 10.45", teacher = "Ibu Ratna Dewi, S.Psi", room = "Ruang BK"),
            ClassSchedule(dayOfWeek = "Jumat", orderNumber = 4, subject = "Jumat Bersih Lab & Sholat Jumat", timeRange = "10.45 - 12.45", teacher = "Wali Kelas & Rohis", room = "Lab & Masjid")
        )
    }

    fun getDefaultPickets(): List<PicketSchedule> {
        return listOf(
            PicketSchedule(dayOfWeek = "Senin", members = "Achmad Fauzan, Alya Nur, Arya Bima, Bagas Pratama, Cantika Dewi", taskDescription = "Piket Lab Jaringan: Rapikan router, kabel patch cord, sapu lantai lab & buang sampah"),
            PicketSchedule(dayOfWeek = "Selasa", members = "Dimas Wahyu, Dinda Putri, Fajar Ilham, Farhan Maulana", taskDescription = "Piket Kelas & Lab: Bersihkan whiteboard, cek saklar listrik & rapikan switch"),
            PicketSchedule(dayOfWeek = "Rabu", members = "Gilang Prasetyo, Hani Octaviana, Irfan Hakim, Kevin Jonathan, Laila Rahma", taskDescription = "Piket Lab Server: Cek suhu AC server, tata kursi lab & bersihkan keyboard"),
            PicketSchedule(dayOfWeek = "Kamis", members = "M. Rizaldi, Nabila Syifa, Pandu Wicaksono, Rahmat Hidayat", taskDescription = "Piket Lab FO: Amankan alat splicing & stripper, sapu lab & kunci lemari rack"),
            PicketSchedule(dayOfWeek = "Jumat", members = "Salma Nur, Tegar Aditya, Vina Anggraeni, Yoga Pratama", taskDescription = "Jumat Bersih Akbar: Matikan semua komputer lab, rapikan meja & buang sampah lab")
        )
    }

    fun getDefaultTransactions(): List<CashTransaction> {
        return listOf(
            CashTransaction(
                title = "Iuran Kas Minggu Ke-8",
                amount = 110000L,
                type = "INCOME",
                date = "18 Sep 2026",
                category = "Kas Mingguan",
                notes = "22 siswa x Rp 5.000 (Lunas semua)"
            ),
            CashTransaction(
                title = "Beli Crimping Tool & 1 Box RJ45 Cat6",
                amount = 125000L,
                type = "EXPENSE",
                date = "16 Sep 2026",
                category = "Peralatan Lab",
                notes = "Tang crimping cadangan kelas + konektor RJ45 untuk praktikum"
            ),
            CashTransaction(
                title = "Beli Spidol Boardmarker & Penghapus Whiteboard",
                amount = 45000L,
                type = "EXPENSE",
                date = "12 Sep 2026",
                category = "Alat Tulis",
                notes = "Spidol hitam & biru Snowman untuk materi di lab"
            ),
            CashTransaction(
                title = "Iuran Kas Minggu Ke-7",
                amount = 110000L,
                type = "INCOME",
                date = "11 Sep 2026",
                category = "Kas Mingguan",
                notes = "22 siswa x Rp 5.000"
            ),
            CashTransaction(
                title = "Keuntungan Jasa Servis PC & Instalasi OS Bazar",
                amount = 350000L,
                type = "INCOME",
                date = "08 Sep 2026",
                category = "Kas Usaha",
                notes = "Hasil jasa instalasi Windows, Linux, dan optimasi laptop siswa"
            ),
            CashTransaction(
                title = "Saldo Awal Kas Semester Ganjil XI-TKJ",
                amount = 650000L,
                type = "INCOME",
                date = "20 Jul 2026",
                category = "Saldo Awal",
                notes = "Sisa kas semester sebelumnya kelas X-TKJ"
            )
        )
    }

    fun getDefaultGallery(): List<GalleryItem> {
        return listOf(
            GalleryItem(
                title = "Praktikum Splicing Fiber Optik Perdana",
                category = "Praktikum",
                date = "16 Sep 2026",
                imageUrl = "",
                description = "Seluruh siswa XI-TKJ sukses menyambung core serat optik dengan redaman mendekati 0.01 dB di Lab Fiber Optic!",
                likesCount = 42,
                isLiked = true
            ),
            GalleryItem(
                title = "Konfigurasi MikroTik OSPF & Hotspot Gateway",
                category = "Praktikum",
                date = "10 Sep 2026",
                imageUrl = "",
                description = "Simulasi routing dinamis antar routerboard dan setting voucher hotspot jaringan sekolah.",
                likesCount = 38,
                isLiked = false
            ),
            GalleryItem(
                title = "Juara 1 Lomba Cepat Tepat Jaringan Komputer",
                category = "Prestasi",
                date = "04 Sep 2026",
                imageUrl = "",
                description = "Tim XI-TKJ berhasil meraih juara 1 dalam ajang kompetisi IT Network tingkat SMK se-kota.",
                likesCount = 56,
                isLiked = true
            ),
            GalleryItem(
                title = "Foto Bersama di Depan Lab TKJ",
                category = "Kenangan",
                date = "25 Agu 2026",
                imageUrl = "",
                description = "Kompak bersama 22 siswa XI-TKJ dan Bapak Wali Kelas Budi Santoso memakai wearpack kebanggaan.",
                likesCount = 68,
                isLiked = true
            )
        )
    }

    fun getDefaultAnnouncements(): List<Announcement> {
        return listOf(
            Announcement(
                title = "Praktikum Routing Dinamis BGP & OSPF",
                category = "Tugas/PR",
                content = "Siapkan topologi di Cisco Packet Tracer dan kumpul file .pkt di Google Classroom paling lambat hari Senin jam 07.00.",
                dueDate = "22 Sep 2026",
                author = "Pak Budi (Wali Kelas)",
                datePosted = "18 Sep 2026",
                isCompleted = false
            ),
            Announcement(
                title = "Iuran Kas Rutin Setiap Jumat Rp 5.000",
                category = "Pengumuman",
                content = "Uang kas mingguan disetorkan ke Cantika atau Hani untuk kas peralatan lab dan kebutuhan kelas.",
                dueDate = "25 Sep 2026",
                author = "Cantika (Bendahara 1)",
                datePosted = "17 Sep 2026",
                isCompleted = true
            ),
            Announcement(
                title = "Ujian Praktik Perakitan PC & Instalasi Proxmox",
                category = "Ujian",
                content = "Penilaian harian kejuruan instalasi Proxmox VE dan konfigurasi container Linux. Siapkan flashdisk bootable masing-masing.",
                dueDate = "28 Sep 2026",
                author = "Pak Agus (Guru ASJ)",
                datePosted = "16 Sep 2026",
                isCompleted = false
            )
        )
    }

    fun getDefaultSpotifyTracks(): List<SpotifyTrackEntity> {
        return listOf(
            SpotifyTrackEntity(
                id = 1,
                title = "Monokrom",
                artist = "Tulus",
                album = "Monokrom",
                durationText = "3:34",
                coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500&q=80",
                audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                spotifyUri = "spotify:track:1"
            ),
            SpotifyTrackEntity(
                id = 2,
                title = "Kisah Klasik",
                artist = "Sheila On 7",
                album = "Kisah Klasik Untuk Masa Depan",
                durationText = "4:15",
                coverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500&q=80",
                audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
                spotifyUri = "spotify:track:2"
            ),
            SpotifyTrackEntity(
                id = 3,
                title = "Ingatlah Hari Ini",
                artist = "Project Pop",
                album = "Pop OK",
                durationText = "4:02",
                coverUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=500&q=80",
                audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
                spotifyUri = "spotify:track:3"
            ),
            SpotifyTrackEntity(
                id = 4,
                title = "Laskar Pelangi",
                artist = "Nidji",
                album = "Breakthru'",
                durationText = "3:37",
                coverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=500&q=80",
                audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
                spotifyUri = "spotify:track:4"
            ),
            SpotifyTrackEntity(
                id = 5,
                title = "Sahabat Kecil",
                artist = "Ipang",
                album = "OST Laskar Pelangi",
                durationText = "3:50",
                coverUrl = "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=500&q=80",
                audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
                spotifyUri = "spotify:track:5"
            )
        )
    }
}
